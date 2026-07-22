package com.nextbuy.adhub.bootstrap.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

@Configuration
public class LocaleConfig {

    public static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

    @Bean
    LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        resolver.setDefaultLocale(DEFAULT_LOCALE);
        resolver.setSupportedLocales(List.of(
                Locale.ENGLISH,
                Locale.forLanguageTag("fa")
        ));
        return resolver;
    }

    @Bean
    MessageSource messageSource() {
        ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
        source.setBasenames(
                "classpath:messages/messages",
                "classpath:messages/ad",
                "classpath:messages/validation"
        );
        source.setDefaultEncoding(StandardCharsets.UTF_8.name());
        source.setFallbackToSystemLocale(false);
        return source;
    }
}