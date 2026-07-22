package com.nextbuy.passport.utils;

import com.nextbuy.passport.dto.RefreshTokenContextDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;


@Service
public class RefreshTokenUtils {

    public RefreshTokenContextDto buildContext(HttpServletRequest request) {
        String ip = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");
        String device = UserAgentUtils.getDeviceInfo(userAgent != null ? userAgent : "");
        return new RefreshTokenContextDto(ip, userAgent, device);
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isBlank()) {
            return xRealIp;
        }
        return request.getRemoteAddr();
    }

}
