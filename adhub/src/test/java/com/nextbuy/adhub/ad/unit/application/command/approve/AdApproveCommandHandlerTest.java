package com.nextbuy.adhub.ad.unit.application.command.approve;

import com.nextbuy.adhub.ad.api.event.AdActivatedIntegrationEvent;
import com.nextbuy.adhub.ad.application.command.approve.AdApproveCommand;
import com.nextbuy.adhub.ad.application.command.approve.AdApproveCommandHandler;
import com.nextbuy.adhub.ad.application.command.approve.AdApproveResult;
import com.nextbuy.adhub.ad.application.event.AdDomainEventPublisher;
import com.nextbuy.adhub.ad.application.event.AdEventMapper;
import com.nextbuy.adhub.ad.domain.model.Ad;
import com.nextbuy.adhub.ad.domain.model.AdId;
import com.nextbuy.adhub.ad.domain.model.AdStatus;
import com.nextbuy.adhub.ad.domain.repository.AdRepository;
import com.nextbuy.adhub.support.ad.fixtures.Ads;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("application:command:approve:AdApproveCommandHandlerTest")
class AdApproveCommandHandlerTest {

    @Mock
    private AdRepository adRepository;

    @Spy
    private AdEventMapper adEventMapper = new AdEventMapper();

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    private AdApproveCommandHandler handler;

    @BeforeEach
    void setUp() {
        handler = new AdApproveCommandHandler(
                adRepository,
                new AdDomainEventPublisher(adEventMapper, applicationEventPublisher));
    }

    @Test
    @DisplayName("It should reject approval when the ad does not exist.")
    void should_Throw_When_AdNotFound() {
        UUID adId = UUID.randomUUID();
        given(adRepository.findById(AdId.of(adId))).willReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(new AdApproveCommand(adId)))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Ad");

        verify(adRepository).findById(AdId.of(adId));
        verify(adRepository, never()).save(org.mockito.ArgumentMatchers.any());
        verify(applicationEventPublisher, never()).publishEvent(org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("It should approve a pending ad, save it, and publish an activation event.")
    void should_ApprovePendingAdAndPublishEvent_When_AdExists() {
        Ad ad = Ads.pendingModeration();
        UUID adId = ad.getId().valueOrThrow();
        given(adRepository.findById(ad.getId())).willReturn(Optional.of(ad));

        Instant before = Instant.now();
        AdApproveResult result = handler.handle(new AdApproveCommand(adId));
        Instant after = Instant.now();

        assertThat(result.adId()).isEqualTo(adId);
        assertThat(result.status()).isEqualTo(AdStatus.ACTIVE.name());
        assertThat(result.updatedAt()).isBetween(before, after);
        verify(adRepository).save(ad);

        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(applicationEventPublisher).publishEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue()).isInstanceOf(AdActivatedIntegrationEvent.class);
        var published = (AdActivatedIntegrationEvent) eventCaptor.getValue();
        assertThat(published.adId()).isEqualTo(adId);
        assertThat(published.expiresAt()).isBetween(
                before.plus(Duration.ofDays(30)),
                after.plus(Duration.ofDays(30)));
        assertThat(published.occurredAt()).isBetween(before, after);
    }
}
