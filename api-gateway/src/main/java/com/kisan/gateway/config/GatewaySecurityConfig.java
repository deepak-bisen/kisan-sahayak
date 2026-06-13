package com.kisan.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class GatewaySecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                // Disable CSRF completely for the gateway.
                // This is required for SPA (Angular) calling JWT-protected APIs with POST/PUT etc.
                // Without this, you get "An expected CSRF token cannot be found" 403 on register/login etc.
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                // We let everything through here. Our custom AuthenticationFilter (applied per-route) handles JWT.
                .authorizeExchange(exchanges -> exchanges
                        .anyExchange().permitAll()
                );
        return http.build();
    }
}