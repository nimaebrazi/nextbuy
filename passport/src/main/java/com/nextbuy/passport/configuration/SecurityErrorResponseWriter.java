package com.nextbuy.passport.configuration;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextbuy.passport.common.advice.model.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SecurityErrorResponseWriter {

    private final ObjectMapper objectMapper;

    public void write(HttpServletResponse response, int status,
                      String errorCode, String message, String path) throws IOException {

        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiResponse<Void> body = ApiResponse.failure(errorCode, message).withPath(path);

        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
