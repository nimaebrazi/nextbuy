package com.nextbuy.passport.configuration;

import com.nextbuy.passport.dto.RateLimitResult;
import com.nextbuy.passport.service.RateLimitService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {
    private final RateLimitService rateLimitService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (!request.getRequestURI().contains("/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        String ip = getClientIp(request);

        RateLimitResult ipResult = rateLimitService.checkIp(ip);
        if (!ipResult.allowed()) {
            writeBlockedResponse(response, ipResult.retryAfterSeconds());
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void writeBlockedResponse(HttpServletResponse response,
                                      long retryAfterSeconds)
            throws IOException {
        response.setStatus(429);
        response.setHeader("Retry-After", String.valueOf(retryAfterSeconds));
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("""
            {
                "code": "TOO_MANY_REQUESTS",
                "message": "Too many attempts. Try again in %d seconds."
            }
            """.formatted(retryAfterSeconds));
        response.getWriter().flush();
    }
    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
