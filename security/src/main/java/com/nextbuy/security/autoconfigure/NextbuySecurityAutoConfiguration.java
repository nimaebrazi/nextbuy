package com.nextbuy.security.autoconfigure;


import com.nextbuy.security.auth.JwtAuthenticationFilter;
import com.nextbuy.security.headers.HeaderAuthenticationFilter;
import com.nextbuy.security.headers.IdentityHeadersFilter;
import com.nextbuy.security.jwt.JwtProperties;
import com.nextbuy.security.jwt.JwtService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(JwtProperties.class)
@ConditionalOnProperty(prefix = "jwt", name = "secret")
public class NextbuySecurityAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "jwt", name = "secret")
    JwtService jwtService(JwtProperties props) {
        if (props.secret() == null || props.secret().isBlank()) {
            throw new IllegalStateException(
                    "jwt.secret must be non-blank (>= 256 bits). "
                            + "An empty JWT_SECRET environment variable overrides application defaults."
            );
        }
        return new JwtService(props);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "jwt", name = "secret")
    JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService) {
        return new JwtAuthenticationFilter(jwtService);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "nextbuy.security", name = "identity-headers", havingValue = "true")
    IdentityHeadersFilter identityHeadersFilter() {
        return new IdentityHeadersFilter();
    }

}
