package com.nextbuy.passport.unit.service;

import com.nextbuy.passport.configuration.RateLimitProperties;
import com.nextbuy.passport.dto.RateLimitResult;
import com.nextbuy.passport.service.RateLimitService;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.distributed.BucketProxy;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.distributed.proxy.RemoteBucketBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@Tags({@Tag("unit"),@Tag("service"),
})
@ExtendWith(MockitoExtension.class)
@DisplayName("RateLimitServiceTest")
class RateLimitServiceTest {

    private static final RateLimitProperties PROPERTIES = new RateLimitProperties(
            new RateLimitProperties.LimitConfig(5, 5, 10), // ip
            new RateLimitProperties.LimitConfig(5, 5, 10)  // user
    );

    @Mock
    private ProxyManager<byte[]> proxyManager;

    @Mock
    private RemoteBucketBuilder<byte[]> remoteBucketBuilder;

    @Mock
    private BucketProxy bucketProxy;

    @Mock
    private ConsumptionProbe probe;

    private RateLimitService rateLimitService;

    @BeforeEach
    void setUp() {
        rateLimitService = new RateLimitService(proxyManager, PROPERTIES);

        given(proxyManager.builder()).willReturn(remoteBucketBuilder);
        given(remoteBucketBuilder.build(any(byte[].class), any(Supplier.class)))
                .willReturn(bucketProxy);
    }

    @Test
    @DisplayName("checkIp should return allowed when bucket consumes a token")
    void checkIp_whenTokenIsAvailable_returnsAllowedWithRemaining() {
        stubAllowedProbe(4L);

        RateLimitResult result = rateLimitService.checkIp("127.0.0.1");

        assertThat(result.allowed()).isTrue();
        assertThat(result.remainingTokens()).isEqualTo(4L);
        assertThat(result.retryAfterSeconds()).isZero();

        verify(proxyManager).builder();
        verify(bucketProxy).tryConsumeAndReturnRemaining(1L);
        verifyNoMoreInteractions(proxyManager, remoteBucketBuilder, bucketProxy, probe);
    }

    @Test
    @DisplayName("checkIp should return blocked with retry-after when bucket is exhausted")
    void checkIp_whenTokenIsUnavailable_returnsBlockedWithRetryAfter() {
        stubBlockedProbe(2_500_000_000L); // ceil → 3 seconds

        RateLimitResult result = rateLimitService.checkIp("127.0.0.1");

        assertThat(result.allowed()).isFalse();
        assertThat(result.remainingTokens()).isZero();
        assertThat(result.retryAfterSeconds()).isEqualTo(3L);

        verify(bucketProxy).tryConsumeAndReturnRemaining(1L);
    }

    @Test
    @DisplayName("checkIp should use login:ip Redis key")
    void checkIp_usesLoginIpKey() {
        stubAllowedProbe(3L);

        rateLimitService.checkIp("10.0.0.1");

        ArgumentCaptor<byte[]> keyCaptor = ArgumentCaptor.forClass(byte[].class);
        verify(remoteBucketBuilder).build(keyCaptor.capture(), any(Supplier.class));

        assertThat(new String(keyCaptor.getValue(), StandardCharsets.UTF_8))
                .isEqualTo("login:ip:10.0.0.1");
    }

    @Test
    @DisplayName("checkUser should return allowed when bucket consumes a token")
    void checkUser_whenTokenIsAvailable_returnsAllowedWithRemaining() {
        stubAllowedProbe(2L);

        RateLimitResult result = rateLimitService.checkUser("user@example.com");

        assertThat(result.allowed()).isTrue();
        assertThat(result.remainingTokens()).isEqualTo(2L);
        assertThat(result.retryAfterSeconds()).isZero();

        verify(bucketProxy).tryConsumeAndReturnRemaining(1L);
    }

    @Test
    @DisplayName("checkUser should return blocked with retry-after when bucket is exhausted")
    void checkUser_whenTokenIsUnavailable_returnsBlockedWithRetryAfter() {
        stubBlockedProbe(1_000_000_000L); // ceil → 1 second

        RateLimitResult result = rateLimitService.checkUser("user@example.com");

        assertThat(result.allowed()).isFalse();
        assertThat(result.remainingTokens()).isZero();
        assertThat(result.retryAfterSeconds()).isEqualTo(1L);
    }

    @Test
    @DisplayName("checkUser should use login:user Redis key")
    void checkUser_usesLoginUserKey() {
        stubAllowedProbe(1L);

        rateLimitService.checkUser("nima@example.com");

        ArgumentCaptor<byte[]> keyCaptor = ArgumentCaptor.forClass(byte[].class);
        verify(remoteBucketBuilder).build(keyCaptor.capture(), any(Supplier.class));

        assertThat(new String(keyCaptor.getValue(), StandardCharsets.UTF_8))
                .isEqualTo("login:user:nima@example.com");
    }

    @Test
    @DisplayName("blocked retry-after should never be less than 1 second")
    void checkIp_whenNanosToWaitIsZero_retryAfterIsAtLeastOne() {
        stubBlockedProbe(0L);

        RateLimitResult result = rateLimitService.checkIp("127.0.0.1");

        assertThat(result.allowed()).isFalse();
        assertThat(result.retryAfterSeconds()).isEqualTo(1L);
    }

    @Test
    @DisplayName("blocked retry-after should round nanos up to whole seconds")
    void checkIp_whenNanosToWaitIsPartialSecond_roundsUpRetryAfter() {
        stubBlockedProbe(1L); // 1 nanosecond → still 1 second after ceil + max(1, ...)

        RateLimitResult result = rateLimitService.checkIp("127.0.0.1");

        assertThat(result.allowed()).isFalse();
        assertThat(result.retryAfterSeconds()).isEqualTo(1L);
    }

    private void stubAllowedProbe(long remainingTokens) {
        given(bucketProxy.tryConsumeAndReturnRemaining(1L)).willReturn(probe);
        given(probe.isConsumed()).willReturn(true);
        given(probe.getRemainingTokens()).willReturn(remainingTokens);
    }

    private void stubBlockedProbe(long nanosToWaitForRefill) {
        given(bucketProxy.tryConsumeAndReturnRemaining(1L)).willReturn(probe);
        given(probe.isConsumed()).willReturn(false);
        given(probe.getNanosToWaitForRefill()).willReturn(nanosToWaitForRefill);
    }
}