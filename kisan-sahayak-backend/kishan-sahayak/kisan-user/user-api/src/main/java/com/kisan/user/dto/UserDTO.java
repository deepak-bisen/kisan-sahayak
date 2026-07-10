package com.kisan.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public class UserDTO {

        private String userId;

        @NotBlank(message = "Full name is required")
        @Size(min = 3, max = 50, message = "Full name must be between 3 and 50 characters")
        private String fullName;

        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "^[6-9]{1}[0-9]{9}$", message = "Phone number must be exactly 10 digits and start with 6, 7, 8, or 9")
        private String phoneNumber;

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters long")
        private String password;

        @NotBlank(message = "Village name is required")
        private String villageName;

        @NotBlank(message = "District is required")
        private String district;

        @NotBlank(message = "State is required")
        private String state;

        @NotBlank(message = "Role is required")
        @Pattern(regexp = "^(FARMER|EQUIPMENT_OWNER|ADMIN)$", message = "Role must be FARMER, EQUIPMENT_OWNER, or ADMIN")
        private String role;

    }