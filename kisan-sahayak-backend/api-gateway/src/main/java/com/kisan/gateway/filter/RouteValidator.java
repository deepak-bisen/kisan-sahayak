package com.kisan.gateway.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    // List of URLs that do NOT require a JWT token
    public static final List<String> openApiEndpoints = List.of(
            "/api/users/register",
            "/api/users/login",
            "/api/knowledge",
            "/eureka",
            "/uploads"
    );

    public Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));
}
