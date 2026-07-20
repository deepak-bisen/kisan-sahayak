package com.kisan.gateway.filter;

import com.kisan.gateway.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final RouteValidator validator;
    private final JwtUtil jwtUtil;

    public AuthenticationFilter(RouteValidator validator, JwtUtil jwtUtil) {
        super(Config.class);
        this.validator = validator;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            if (exchange.getRequest().getMethod() == HttpMethod.OPTIONS) {
                return chain.filter(exchange);
            }

            // 1. Check if the current route requires authentication
            if (validator.isSecured.test(exchange.getRequest())) {

                // 2. Check if the Authorization header is present
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }

                // 3. Get the header value (looks like: "Bearer abcdef12345...")
                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7); // Remove the word "Bearer " to isolate the token
                }

                // 4. Validate the token
                try {
                    jwtUtil.validateToken(authHeader);
                } catch (Exception e) {
                    log.warn("Invalid access token intercepted!");
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }
            }

            // If everything is good, forward the request to the target microservice
            return chain.filter(exchange);
        });
    }

    public static class Config {
        // Empty class required by Spring Cloud Gateway
    }
}
