package com.nextbuy.passport.common.advice;

import com.nextbuy.passport.common.advice.model.ApiResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
public class ResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return !GlobalExceptionHandler.class.isAssignableFrom(returnType.getContainingClass());
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {

        if (body instanceof ApiResponse<?> apiResponse) {
            if (apiResponse.getPath() == null) {
                apiResponse.setPath(request.getURI().getPath());
            }
            return apiResponse;
        }

        if (body instanceof String) {
            return body;
        }

        return ApiResponse.success(body).withPath(request.getURI().getPath());
    }
}
