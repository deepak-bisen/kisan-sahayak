package com.kisan.marketplace.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "equipments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Equipment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String equipmentId;

    @Column(nullable = false)
    public String equipmentName;  // e.g., "Mahindra Tractor 575 DI"

    private String description;

    @Column(nullable = false)
    public String category;  // e.g., TRACTOR, HARVESTER, PLOUGH

    @Column(nullable = false)
    private BigDecimal hourlyRate;

    @Column(nullable = false)
    private BigDecimal dailyRate;

    @Column(nullable = false)
    private String ownerId; // Links to userId in User-Service

    // Basic location caching to avoid querying User-Service too often during searches
    private String villageName;
    private String district;
    private Double latitude;
    private Double longitude;

    @Builder.Default
    private boolean isAvailable = true;
}
