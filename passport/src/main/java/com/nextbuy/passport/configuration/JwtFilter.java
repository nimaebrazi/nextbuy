package com.nextbuy.passport.configuration;

import com.nextbuy.passport.service.JwtService;
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
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

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

    private Principals toPrincipals(JwtService.JwtClaimsDto claims) {
        return new Principals(
                claims.userId(),
                claims.email(),
                parseAuthorities(claims.roles())
        );
    }


    private List<SimpleGrantedAuthority> parseAuthorities(String roles) {
        if (roles == null || roles.isBlank()) {
            return List.of();
        }

        return Arrays.stream(roles.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(SimpleGrantedAuthority::new)
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

    public record AuthPrincipal(Long id, String email) {
    }
}
