package com.kisan.marketplace.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "equipments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Equipment {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "NAME", columnDefinition = "VARCHAR(50)", nullable = false)
    private String name;  // e.g., "Mahindra Tractor 575 DI"

    @Column(name = "DESCRIPTION", columnDefinition = "VARCHAR(255)", nullable = true)
    private String description;

    @Column(name = "CATEGORY", columnDefinition = "VARCHAR(50)", nullable = true)
    private String category;  // e.g., TRACTOR, HARVESTER, PLOUGH

    @Column(name = "HOURLY_RATE", nullable = false)
    private BigDecimal hourlyRate;

    @Column(name = "DAILY_RATE", nullable = false)
    private BigDecimal dailyRate;

    @Column(name = "OWNER_ID", columnDefinition = "VARCHAR(40)", nullable = false)
    private String ownerId; // Links to userId in User-Service

    @Column(name = "OWNER_NAME")
    private String ownerName; // Cached at creation from user service

    // Store the image URL
    @Column(name = "IMAGE_URL", columnDefinition = "VARCHAR(255)", nullable = false)
    private String imageUrl;

    // Basic location caching to avoid querying User-Service too often during searches
    private String villageName;
    private String district;
    private Double latitude;
    private Double longitude;

    @Builder.Default
    private boolean isAvailable = true;

    @Column(name = "CREATED_AT", columnDefinition = "DATETIME",nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT", columnDefinition = "DATETIME",nullable = true)
    private LocalDateTime updatedAt;
}
