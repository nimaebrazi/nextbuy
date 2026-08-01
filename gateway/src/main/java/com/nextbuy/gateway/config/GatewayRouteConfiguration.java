package com.nextbuy.gateway.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.stripPrefix;
import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;

@Configuration
@RequiredArgsConstructor
public class GatewayRouteConfiguration {

    private final GatewayProperties gatewayProps;

    @Bean
    RouterFunction<ServerResponse> passportPublicRoutes() {
        return route("passport-auth-public")
                .route(path(
                        "/passport/api/v1/login",
                        "/passport/api/v1/register",
                        "/passport/api/v1/refresh-token"
                ), http())
                .before(stripPrefix(1))
                .before(uri(gatewayProps.passportUri()))
                .build();
    }

    @Bean
    RouterFunction<ServerResponse> passportSecuredRoutes() {
        return route("passport-auth-secured")
                .route(path(
                        "/passport/api/v1/profile",
                        "/passport/api/v1/logout"
                ), http())
                .before(stripPrefix(1))
                .before(uri(gatewayProps.passportUri()))
                .build();
    }

    @Bean
    RouterFunction<ServerResponse> adhubRoutes() {
        return route("adhub")
                .route(path("/adhub/api/v1/ads/**"), http())
                .before(stripPrefix(1))
                .before(uri(gatewayProps.adhubUri()))
                .build();
    }

}
