package com.kisan.user.dto;

import lombok.*;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class UserDTO {
        private String userId;
        private String fullName;
        private String phoneNumber;
        private String villageName;
        private String district;
        private String role; // e.g., FARMER, EQUIPMENT_OWNER
    }