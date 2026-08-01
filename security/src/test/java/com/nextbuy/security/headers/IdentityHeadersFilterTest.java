package com.nextbuy.security.headers;

import com.nextbuy.security.auth.AuthPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IdentityHeadersFilterTest {

    private IdentityHeadersFilter identityHeadersFilter;
    private CapturingFilterChain chain;

    @BeforeEach
    void setUp() {
        identityHeadersFilter = new IdentityHeadersFilter();
        chain = new CapturingFilterChain();
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void authenticated_injectsTrustedIdentityHeaders() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        new AuthPrincipal(42L, "u@nextbuy.com"),
                        null,
                        List.of(
                                new SimpleGrantedAuthority("ROLE_USER"),
                                new SimpleGrantedAuthority("AD_APPROVE")
                        )
                )
        );

        MockHttpServletRequest request = spoofedRequest();
        request.addHeader("X-Other", "keep-me");

        identityHeadersFilter.doFilter(request, new MockHttpServletResponse(), chain);

        HttpServletRequest wrapped = chain.request();
        assertEquals("42", wrapped.getHeader(IdentityHeaderNames.USER_ID));
        assertEquals("u@nextbuy.com", wrapped.getHeader(IdentityHeaderNames.USER_EMAIL));
        assertEquals("ROLE_USER", wrapped.getHeader(IdentityHeaderNames.USER_ROLES));
        assertEquals("AD_APPROVE", wrapped.getHeader(IdentityHeaderNames.USER_PERMISSIONS));
        assertEquals("keep-me", wrapped.getHeader("X-Other"));
        assertTrue(Collections.list(wrapped.getHeaderNames()).stream()
                .anyMatch(name -> name.equalsIgnoreCase(IdentityHeaderNames.USER_ID)));
        assertFalse(Collections.list(wrapped.getHeaders(IdentityHeaderNames.USER_ROLES)).stream()
                .anyMatch(value -> value.contains("AD_APPROVE")));
    }

    @Test
    void anonymous_stripsSpoofedIdentityHeaders() throws Exception {
        MockHttpServletRequest request = spoofedRequest();

        identityHeadersFilter.doFilter(request, new MockHttpServletResponse(), chain);

        HttpServletRequest wrapped = chain.request();
        assertNull(wrapped.getHeader(IdentityHeaderNames.USER_ID));
        assertNull(wrapped.getHeader(IdentityHeaderNames.USER_EMAIL));
        assertNull(wrapped.getHeader(IdentityHeaderNames.USER_ROLES));
        assertNull(wrapped.getHeader(IdentityHeaderNames.USER_PERMISSIONS));
        assertFalse(Collections.list(wrapped.getHeaderNames()).stream()
                .anyMatch(IdentityHeadersFilterTest::isManagedIdentityHeader));
    }

    @Test
    void authenticated_withoutAuthPrincipal_stripsOnly() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "not-an-auth-principal",
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                )
        );

        MockHttpServletRequest request = spoofedRequest();

        identityHeadersFilter.doFilter(request, new MockHttpServletResponse(), chain);

        HttpServletRequest wrapped = chain.request();
        assertNull(wrapped.getHeader(IdentityHeaderNames.USER_ID));
        assertNull(wrapped.getHeader(IdentityHeaderNames.USER_EMAIL));
        assertNull(wrapped.getHeader(IdentityHeaderNames.USER_ROLES));
        assertNull(wrapped.getHeader(IdentityHeaderNames.USER_PERMISSIONS));
    }

    private static MockHttpServletRequest spoofedRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(IdentityHeaderNames.USER_ID, "999");
        request.addHeader(IdentityHeaderNames.USER_EMAIL, "spoof@evil.com");
        request.addHeader(IdentityHeaderNames.USER_ROLES, "ROLE_ADMIN");
        request.addHeader(IdentityHeaderNames.USER_PERMISSIONS, "AD_APPROVE,EVERYTHING");

        return request;
    }

    private static boolean isManagedIdentityHeader(String name) {
        return name.equalsIgnoreCase(IdentityHeaderNames.USER_ID)
                || name.equalsIgnoreCase(IdentityHeaderNames.USER_EMAIL)
                || name.equalsIgnoreCase(IdentityHeaderNames.USER_ROLES)
                || name.equalsIgnoreCase(IdentityHeaderNames.USER_PERMISSIONS);
    }

    private static final class CapturingFilterChain implements FilterChain {
        private HttpServletRequest request;

        @Override
        public void doFilter(ServletRequest request, ServletResponse response)
                throws IOException, ServletException {
            this.request = (HttpServletRequest) request;
        }

        HttpServletRequest request() {
            return request;
        }
    }
}
