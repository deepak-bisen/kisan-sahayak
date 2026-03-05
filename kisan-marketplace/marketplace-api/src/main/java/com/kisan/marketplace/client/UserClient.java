package com.kisan.marketplace.client;

import com.kisan.marketplace.dto.UserResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign Client used to communicate with the 'kisan-user' microservice via Eureka.
 */
@FeignClient(name = "kisan-user", path = "/api/users")
public interface UserClient {

    @GetMapping("/{id}")
    UserResponseDTO getUserById(@PathVariable("id") String id);
}
