package com.kisan.marketplace.dto;

import lombok.Data;

/**
 * A lightweight DTO to capture the response from the User Service via Feign.
 * We only need specific fields like role and location to cache in the Marketplace.
 */
@Data
public class UserResponseDTO {
    private String userId;
    private String fullName;
    private String role;
    private String villageName;
    private String district;
}
