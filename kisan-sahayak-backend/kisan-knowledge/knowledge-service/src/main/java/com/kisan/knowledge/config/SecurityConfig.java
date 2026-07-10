package com.kisan.knowledge.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF explicitly using lambda syntax
                .cors(AbstractHttpConfigurer::disable) // Disable CORS checks which can also trigger 403 Forbidden
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // Let our API Gateway handle the actual JWT security!
                );
        return http.build();
    }
}
