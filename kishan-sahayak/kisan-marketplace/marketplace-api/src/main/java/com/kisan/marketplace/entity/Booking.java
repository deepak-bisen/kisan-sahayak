package com.kisan.marketplace.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "EQUIPMENT_ID", columnDefinition = "VARCHAR(40)", nullable = false)
    private String equipmentId; // Which equipment is being booked

    @Column(name = "RENTER_ID", columnDefinition = "VARCHAR(40)", nullable = false)
    private String renterId; // Links to userId in User-Service (the farmer renting it)

    @Column(name = "START_DATE", columnDefinition = "DATETIME", nullable = false)
    private LocalDate startDate;

    @Column(name = "END_DATE", columnDefinition = "DATETIME", nullable = false)
    private LocalDate endDate;

    @Column(name = "TOTAL_COST", nullable = false)
    private BigDecimal totalCost;

    @Column(nullable = false)
    private String status; // e.g., REQUESTED, CONFIRMED, COMPLETED, CANCELLED
}
