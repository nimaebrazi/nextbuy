package com.nextbuy.passport.common.advice.annotation;

import com.nextbuy.passport.common.advice.config.ApiHandlerConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(ApiHandlerConfig.class)
public @interface EnableApiHandler {
}
