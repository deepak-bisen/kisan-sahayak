package com.kisan.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "USERS")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String userId;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Column(nullable = false)
    private String password;

    @Column
    private String villageName;

    @Column
    private String district;

    @Column
    private String state;

    @Column(nullable = false)
    private String role; // Role can be FARMER, EQUIPMENT_OWNER, or ADMIN

    // Latitude and Longitude for location-based services in the marketplace
    private Double latitude;
    private Double longitude;
}