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
    @GeneratedValue(strategy = GenerationType.UUID)
    private String bookingId;

    @Column(nullable = false)
    private String equipmentId; // Which equipment is being booked

    @Column(nullable = false)
    private String renterId; // Links to userId in User-Service (the farmer renting it)

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private BigDecimal totalCost;

    @Column(nullable = false)
    private String status; // e.g., REQUESTED, CONFIRMED, COMPLETED, CANCELLED
}
