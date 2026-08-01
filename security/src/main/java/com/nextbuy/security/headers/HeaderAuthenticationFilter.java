package com.nextbuy.security.headers;

import com.nextbuy.security.auth.AuthPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class HeaderAuthenticationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            String userIdHeader = request.getHeader(IdentityHeaderNames.USER_ID);

            if (userIdHeader != null && !userIdHeader.isBlank()) {

                Long userId = Long.valueOf(userIdHeader.trim());
                String email = blankToNull(request.getHeader(IdentityHeaderNames.USER_EMAIL));

                List<SimpleGrantedAuthority> authorities = parseAuthorities(
                        request.getHeader(IdentityHeaderNames.USER_ROLES),
                        request.getHeader(IdentityHeaderNames.USER_PERMISSIONS)
                );

                var authentication = new UsernamePasswordAuthenticationToken(
                        new AuthPrincipal(userId, email), null, authorities
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);

            }

        } catch (Exception e) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);

    }

    private List<SimpleGrantedAuthority> parseAuthorities(String roles, String permissions) {
        Set<String> names = new LinkedHashSet<>();
        names.addAll(parseCsv(roles));
        names.addAll(parseCsv(permissions));
        return names.stream().map(SimpleGrantedAuthority::new).toList();
    }

    private static List<String> parseCsv(String csv) {
        if (csv == null || csv.isBlank()) {
            return List.of();
        }
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    private static String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
