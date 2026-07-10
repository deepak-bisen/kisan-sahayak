package com.kisan.marketplace.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipmentDTO {

        private String equipmentId;

        @NotBlank(message = "Equipment name is required")
        private String name;

        private String description;

        @NotBlank(message = "Category is required")
        private String category;

        @NotNull(message = "Hourly rate is required")
        @Positive(message = "Rate must be positive")
        private BigDecimal hourlyRate;

        @NotNull(message = "Daily rate is required")
        @Positive(message = "Rate must be positive")
        private BigDecimal dailyRate;

        @NotBlank(message = "Owner ID is required")
        private String ownerId;

        //Add image URL to the DTO
        private String imageUrl;

        // Read-only location details (populated by the service)
        private String villageName;
        private String district;
        private boolean isAvailable;
    }