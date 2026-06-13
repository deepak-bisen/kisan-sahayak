package com.kisan.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

@Configuration
@EnableWebFluxSecurity
public class GatewaySecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                // For SPA + JWT, we disable CSRF requirement for open auth endpoints (register, login).
                // The CsrfWebFilter will not require token for these paths, preventing 403 CSRF errors.
                // For other paths, if needed, but since we permitAll, and services handle, we can ignore all or specific.
                .csrf(csrf -> csrf
                        .requireCsrfProtectionMatcher(
                                ServerWebExchangeMatchers.not(
                                        ServerWebExchangeMatchers.pathMatchers(
                                                "/api/users/register",
                                                "/api/users/login",
                                                "/eureka/**"
                                        )
                                )
                        )
                )
                // Permit all at security level; the custom filter (if applied in route) handles JWT for secured routes.
                .authorizeExchange(exchanges -> exchanges
                        .anyExchange().permitAll()
                );
        return http.build();
    }
}