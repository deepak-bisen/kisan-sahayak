package com.kisan.knowledge.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CropGuideDTO {

    private String guideId;

    @NotBlank(message = "Crop name is required")
    private String cropName;

    @NotBlank(message = "Season is required (e.g., Rabi, Kharif)")
    private String season;

    @NotBlank(message = "Soil type is required")
    private String soilType;

    @NotNull(message = "Duration in days is required")
    @Min(value = 1, message = "Duration must be at least 1 day")
    private Integer durationInDays;

    @NotBlank(message = "Best practices description is required")
    private String bestPractices;

    private String diseaseManagement;
}