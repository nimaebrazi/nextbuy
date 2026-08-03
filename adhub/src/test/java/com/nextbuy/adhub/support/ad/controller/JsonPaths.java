package com.nextbuy.adhub.support.ad.controller;

import org.springframework.test.web.servlet.ResultMatcher;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public final class JsonPaths {
    private JsonPaths() {
    }

    public static ResultMatcher statusOk() {
        return status().isOk();
    }

    public static ResultMatcher isSuccess() {
        return jsonPath(ApiJsonPaths.SUCCESS).value(true);
    }

    public static ResultMatcher isNotSuccess() {
        return jsonPath(ApiJsonPaths.SUCCESS).value(false);
    }

    public static ResultMatcher statusBadRequest() {
        return status().isBadRequest();
    }

    public static ResultMatcher statusUnauthorized() {
        return status().isUnauthorized();
    }

    public static ResultMatcher statusNotImplemented() {
        return status().isNotImplemented();
    }

    public static ResultMatcher statusUnprocessableEntity() {
        return status().isUnprocessableContent();
    }

    public static ResultMatcher statusNotFound() {
        return status().isNotFound();
    }

    public static ResultMatcher message(String message) {
        return jsonPath(ApiJsonPaths.MESSAGE).value(message);
    }

    public static ResultMatcher errorCode(String errorCode) {
        return jsonPath(ApiJsonPaths.ERROR_CODE).value(errorCode);
    }

    public static ResultMatcher data(String path, Object value) {
        return jsonPath("%s.%s".formatted(ApiJsonPaths.DATA, path)).value(value);
    }

    public static ResultMatcher path(String path) {
        return jsonPath(ApiJsonPaths.PATH).value(path);
    }

    public static ResultMatcher[] apiStructure() {
        return new ResultMatcher[]{
                jsonPath(ApiJsonPaths.SUCCESS).exists(),
                jsonPath(ApiJsonPaths.SUCCESS).isBoolean(),

                jsonPath(ApiJsonPaths.MESSAGE).exists(),
                jsonPath(ApiJsonPaths.MESSAGE).isString(),

                jsonPath(ApiJsonPaths.DATA).hasJsonPath(),

                jsonPath(ApiJsonPaths.ERROR_CODE).hasJsonPath(),
                jsonPath(ApiJsonPaths.VALIDATION_ERRORS).hasJsonPath(),

                jsonPath(ApiJsonPaths.TIMESTAMP).exists(),
                jsonPath(ApiJsonPaths.TIMESTAMP).isString(),

                jsonPath(ApiJsonPaths.PATH).exists(),
                jsonPath(ApiJsonPaths.PATH).isString()
        };
    }

    public static ResultMatcher[] successEnvelope(String path, String message) {
        return concat(
                new ResultMatcher[]{statusOk()},
                apiStructure(),
                new ResultMatcher[]{isSuccess(), path(path), message(message)}
        );
    }

    public static ResultMatcher[] errorEnvelope(
            ResultMatcher status, String errorCode, String message, String path) {
        return new ResultMatcher[]{
                status,
                isNotSuccess(),
                errorCode(errorCode),
                message(message),
                path(path)
        };
    }

    public static ResultMatcher[] domainRuleViolation(String path, String message) {
        return errorEnvelope(statusUnprocessableEntity(), "DOMAIN_RULE_VIOLATION", message, path);
    }

    public static ResultMatcher[] entityNotFound(String path) {
        return errorEnvelope(statusNotFound(), "ENTITY_NOT_FOUND", "Resource not found.", path);
    }

    public static ResultMatcher[] validationError(String path, String message) {
        return errorEnvelope(statusUnprocessableEntity(), "VALIDATION_ERROR", message, path);
    }

    @SafeVarargs
    private static ResultMatcher[] concat(ResultMatcher[]... groups) {
        int size = 0;
        for (ResultMatcher[] group : groups) {
            size += group.length;
        }
        ResultMatcher[] result = new ResultMatcher[size];
        int offset = 0;
        for (ResultMatcher[] group : groups) {
            System.arraycopy(group, 0, result, offset, group.length);
            offset += group.length;
        }
        return result;
    }
}
