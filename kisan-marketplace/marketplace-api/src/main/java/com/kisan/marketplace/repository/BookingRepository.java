package com.kisan.marketplace.repository;

import com.kisan.marketplace.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {

    // Find all bookings made by a specific farmer
    List<Booking> findByRenterId(String renterId);

    // Find all bookings for a specific piece of equipment (for the owner to manage)
    List<Booking> findByEquipmentId(String equipmentId);
}
