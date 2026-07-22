package com.nextbuy.adhub.bootstrap.advice;

import com.nextbuy.adhub.bootstrap.advice.model.ApiResponse;
import com.nextbuy.adhub.shared.advice.annotation.SuccessMessage;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class ResponseAdvice implements ResponseBodyAdvice<Object> {

    private final MessageSource messageSource;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return !GlobalExceptionHandler.class.isAssignableFrom(returnType.getContainingClass());
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  @NonNull MethodParameter returnType,
                                  @NonNull MediaType selectedContentType,
                                  @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  @NonNull ServerHttpRequest request,
                                  @NonNull ServerHttpResponse response) {

        if (body instanceof String) {
            return body;
        }

        String path = request.getURI().getPath();
        String message = resolveMessage(returnType);

        if (body instanceof ApiResponse<?> apiResponse) {
            if (apiResponse.getPath() == null || apiResponse.getPath().isEmpty()) {
                apiResponse.setPath(path);
            }

            SuccessMessage ann = returnType.getMethodAnnotation(SuccessMessage.class);
            if (ann != null) {
                apiResponse.setMessage(message);
            }
            return apiResponse;
        }
        return ApiResponse.success(message, body).withPath(path);

    }

    private String resolveMessage(MethodParameter returnType) {
        SuccessMessage ann = returnType.getMethodAnnotation(SuccessMessage.class);
        String key = ann != null ? ann.value() : "operation.success";
        Object[] args = ann != null ? ann.args() : null;
        return messageSource.getMessage(
                key,
                args,
                key, // fallback if key missing
                LocaleContextHolder.getLocale()
        );
    }
}
