package com.kisan.marketplace.repository;

import com.kisan.marketplace.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {

    // Find all bookings made by a specific farmer
    List<Booking> findByRenterId(String renterId);

    // Find all bookings for a specific piece of equipment (for the owner to manage)
    List<Booking> findByEquipmentId(String equipmentId);

    // NEW QUERY: Checks if there is any overlapping booking for the given date that isn't cancelled.
    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.equipmentId = :equipmentId " +
            "AND b.status IN ('REQUESTED', 'CONFIRMED') " +
            "AND b.startDate <= :endDate " +
            "AND b.endDate >= :startDate")
    boolean hasOverlappingBookings(@Param("equipmentId") String equipmentId,
                                   @Param("startDate") LocalDate startDate,
                                   @Param("endDate") LocalDate endDate);
}
