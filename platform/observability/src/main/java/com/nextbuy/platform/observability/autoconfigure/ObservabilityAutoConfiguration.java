package com.nextbuy.platform.observability.autoconfigure;


import com.nextbuy.platform.observability.correlation.CorrelationIdFilter;
import com.nextbuy.platform.observability.correlation.ObservabilityProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;

@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties(ObservabilityProperties.class)
public class ObservabilityAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(
            prefix = "nextbuy.observability.correlation",
            name= "enabled",
            havingValue = "true",
            matchIfMissing = true
    )
    public FilterRegistrationBean<CorrelationIdFilter> correlationIdFilter() {
        FilterRegistrationBean<CorrelationIdFilter> registration =
                new FilterRegistrationBean<>(new  CorrelationIdFilter());
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }

}
