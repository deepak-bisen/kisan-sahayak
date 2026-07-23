package com.kisan.marketplace.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserResponseDTO {
    private String userId;
    private String fullName;
    private String phoneNumber;
    private List<String> roles;
    private String villageName;
    private String district;
}
