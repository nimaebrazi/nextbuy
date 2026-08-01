package com.nextbuy.security.auth;

import com.nextbuy.security.jwt.JwtClaims;
import com.nextbuy.security.jwt.JwtService;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        try {
            getJwtToken(request)
                    .flatMap(jwtService::extractToken)
                    .map(this::toPrincipals)
                    .map(this::createAuthenticationToken)
                    .ifPresent(authentication -> SecurityContextHolder.getContext().setAuthentication(authentication));
        } catch (Exception ex) {
            log.error("Could not set user authentication in security context", ex);
            SecurityContextHolder.clearContext();
        }

        chain.doFilter(request, response);
    }

    private Principals toPrincipals(JwtClaims claims) {
        return new Principals(
                claims.userId(),
                claims.email(),
                parseAuthorities(claims.roles(), claims.permissions())
        );
    }


    private List<SimpleGrantedAuthority> parseAuthorities(String roles, String permissions) {
        // Backward compatibility: legacy tokens may include mixed authorities only in roles.
        if (permissions == null || permissions.isBlank()) {
            return parseAuthoritiesCsv(roles);
        }

        Set<String> mergedAuthorities = new LinkedHashSet<>();
        mergedAuthorities.addAll(parseAuthorityNames(roles));
        mergedAuthorities.addAll(parseAuthorityNames(permissions));

        return mergedAuthorities.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    private List<SimpleGrantedAuthority> parseAuthoritiesCsv(String csv) {
        return parseAuthorityNames(csv).stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    private List<String> parseAuthorityNames(String csv) {
        if (csv == null || csv.isBlank()) {
            return List.of();
        }

        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    private UsernamePasswordAuthenticationToken createAuthenticationToken(Principals principals) {
        AuthPrincipal principal = new AuthPrincipal(principals.userId(), principals.email());

        return new UsernamePasswordAuthenticationToken(
                principal,
                null,
                principals.authorities()
        );
    }

    private Optional<String> getJwtToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("Authorization"))
                .filter(header -> header.startsWith("Bearer "))
                .map(header -> header.substring(7).trim())
                .filter(token -> !token.isBlank());
    }

    public record Principals(
            Long userId,
            String email,
            @Nonnull List<SimpleGrantedAuthority> authorities
    ) {
    }
}
