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

    public static ResultMatcher message(String message) {
        return jsonPath(ApiJsonPaths.MESSAGE).value(message);
    }

    public static ResultMatcher errorCode(String errorCode) {
        return jsonPath(ApiJsonPaths.ERROR_CODE).value(errorCode);
    }

    public static ResultMatcher id(String id) {
        return jsonPath(ApiJsonPaths.ID).value(id);
    }

    public static ResultMatcher createdAt(String createdAt) {
        return jsonPath(ApiJsonPaths.CREATED_AT).value(createdAt);
    }

    public static ResultMatcher adStatus(String status) {
        return jsonPath(ApiJsonPaths.STATUS).value(status);
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
}
