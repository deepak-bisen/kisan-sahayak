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
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "NAME", columnDefinition = "VARCHAR(30)", nullable = false)
    private String name;

    @Column(name = "PHONE_NUMBER", columnDefinition = "VARCHAR(13)", nullable = false)
    private String phoneNumber;

    @Column(name = "PASSWORD", columnDefinition = "VARCHAR(200)", nullable = false)
    private String password;

    @Column(name = "ADDRESS", columnDefinition = "VARCHAR(50)", nullable = false)
    private String villageName;

    @Column(name = "District", columnDefinition = "VARCHAR(30)", nullable = false)
    private String district;

    @Column(name = "STATE", columnDefinition = "VARCHAR(30)", nullable = true)
    private String state;

    @Column(name = "ROLE", columnDefinition = "VARCHAR(20)", nullable = false)
    private String role; // Role can be FARMER, EQUIPMENT_OWNER, or ADMIN

    // Latitude and Longitude for location-based services in the marketplace
    private Double latitude;
    private Double longitude;
}