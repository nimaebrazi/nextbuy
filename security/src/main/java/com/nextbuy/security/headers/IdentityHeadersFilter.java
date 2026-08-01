package com.nextbuy.security.headers;

import com.nextbuy.security.auth.AuthPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class IdentityHeadersFilter extends OncePerRequestFilter {

    private static final Set<String> MANAGED_HEADERS = Set.of(
            IdentityHeaderNames.USER_ID.toLowerCase(),
            IdentityHeaderNames.USER_EMAIL.toLowerCase(),
            IdentityHeaderNames.USER_ROLES.toLowerCase(),
            IdentityHeaderNames.USER_PERMISSIONS.toLowerCase()
    );

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        filterChain.doFilter(new IdentityRequestWrapper(request, resolveInjectedHeaders()), response);
    }

    private static Map<String, String> resolveInjectedHeaders() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthPrincipal principal)) {
            return Map.of();
        }

        Map<String, String> injected = new LinkedHashMap<>();
        injected.put(IdentityHeaderNames.USER_ID, String.valueOf(principal.id()));
        injected.put(IdentityHeaderNames.USER_EMAIL, principal.email());
        injected.put(IdentityHeaderNames.USER_ROLES, getRoles(authentication));
        injected.put(IdentityHeaderNames.USER_PERMISSIONS, getPermissions(authentication));
        return Map.copyOf(injected);
    }

    private static String getPermissions(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority != null && !authority.startsWith("ROLE_"))
                .collect(Collectors.joining(","));
    }

    private static String getRoles(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority != null && authority.startsWith("ROLE_"))
                .collect(Collectors.joining(","));
    }

    private static final class IdentityRequestWrapper extends HttpServletRequestWrapper {

        private final Map<String, String> injectedHeaders;

        private IdentityRequestWrapper(HttpServletRequest request, Map<String, String> injectedHeaders) {
            super(request);
            this.injectedHeaders = injectedHeaders;
        }

        @Override
        public String getHeader(String name) {
            if (isManaged(name)) {
                return findInjected(name);
            }
            return super.getHeader(name);
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            if (isManaged(name)) {
                String value = findInjected(name);
                return value == null
                        ? Collections.emptyEnumeration()
                        : Collections.enumeration(List.of(value));
            }
            return super.getHeaders(name);
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            Set<String> names = new LinkedHashSet<>();
            Enumeration<String> original = super.getHeaderNames();
            while (original.hasMoreElements()) {
                String name = original.nextElement();
                if (!isManaged(name)) {
                    names.add(name);
                }
            }
            names.addAll(injectedHeaders.keySet());
            return Collections.enumeration(names);
        }

        private String findInjected(String name) {
            for (Map.Entry<String, String> entry : injectedHeaders.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(name)) {
                    return entry.getValue();
                }
            }
            return null;
        }

        private static boolean isManaged(String name) {
            return name != null && MANAGED_HEADERS.contains(name.toLowerCase());
        }
    }
}
