package com.kisan.knowledge.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "crop_guides")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CropGuide {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String guideId;

    @Column(nullable = false, unique = true)
    private String cropName; // e.g., "Wheat", "Rice", "Sugarcane"

    @Column(nullable = false)
    private String season; // e.g., "Rabi", "Kharif", "Zaid"

    @Column(nullable = false)
    private String soilType; // e.g., "Alluvial", "Black Soil", "Loamy"

    @Column(nullable = false)
    private Integer durationInDays; // e.g., 120 (days from sowing to harvest)

    @Column(columnDefinition = "TEXT", nullable = false)
    private String bestPractices; // Detailed text about irrigation, fertilizers, etc.

    @Column(columnDefinition = "TEXT")
    private String diseaseManagement; // Tips on handling common pests
}
