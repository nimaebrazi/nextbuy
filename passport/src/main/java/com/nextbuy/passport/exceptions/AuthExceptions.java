package com.nextbuy.passport.exceptions;


import com.nextbuy.passport.common.advice.exception.BusinessException;
import org.springframework.http.HttpStatus;

public final class AuthExceptions {

    private AuthExceptions() {
    }

    public static BusinessException emailAlreadyExist() {
        return new BusinessException(
                "Email already exists",
                "EMAIL_ALREADY_EXISTS",
                HttpStatus.CONFLICT.value()
        );
    }

    public static BusinessException roleNotFound() {
        return new BusinessException(
                "Role not found",
                "ROLE_NOT_FOUND",
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
    }

    public static BusinessException invalidRefreshToken() {
        return new BusinessException(
                "Invalid or expired refresh token",
                "INVALID_REFRESH_TOKEN",
                HttpStatus.UNAUTHORIZED.value()
        );
    }

    public static BusinessException invalidAccessToken() {
        return new BusinessException(
                "Invalid or expired access token",
                "INVALID_ACCESS_TOKEN",
                HttpStatus.UNAUTHORIZED.value()
        );
    }

    public static BusinessException refreshTokenRequired() {
        return new BusinessException(
                "Refresh token is required",
                "REFRESH_TOKEN_REQUIRED",
                HttpStatus.BAD_REQUEST.value()
        );
    }

    public static BusinessException tooManyRequests(long retryAfterSeconds) {
        return new BusinessException(
                "Too many attempts. Try again in " + retryAfterSeconds + " seconds.",
                "TOO_MANY_REQUESTS",
                429
        );
    }

    public static BusinessException refreshTokenReuseDetected() {
        return new BusinessException(
                "Suspicious refresh token reuse detected. Please log in again.",
                "REFRESH_TOKEN_REUSE",
                HttpStatus.UNAUTHORIZED.value()
        );
    }
}
