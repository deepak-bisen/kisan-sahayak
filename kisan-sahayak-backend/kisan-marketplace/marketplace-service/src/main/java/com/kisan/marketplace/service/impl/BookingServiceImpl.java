package com.kisan.marketplace.service.impl;

import com.kisan.marketplace.client.UserClient;
import com.kisan.marketplace.dto.BookingDTO;
import com.kisan.marketplace.dto.UserResponseDTO;
import com.kisan.marketplace.entity.Booking;
import com.kisan.marketplace.entity.Equipment;
import com.kisan.marketplace.enums.Status;
import com.kisan.marketplace.repository.BookingRepository;
import com.kisan.marketplace.repository.EquipmentRepository;
import com.kisan.marketplace.repository.NotificationRepository;
import com.kisan.marketplace.service.BookingService;
import com.kisan.marketplace.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final EquipmentRepository equipmentRepository;
    private final NotificationRepository notificationRepository;
    private final UserClient userClient;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public BookingDTO createBooking(BookingDTO bookingDTO) {
        long days = ChronoUnit.DAYS.between(bookingDTO.getStartDate(), bookingDTO.getEndDate());
        if (days < 0) {
            throw new RuntimeException("End date cannot be before the start date.");
        }
        long totalDays = days == 0 ? 1 : days + 1; // Count same-day as 1 full day

        // 2. Verify Equipment exists and is available
        Equipment equipment = equipmentRepository.findById(bookingDTO.getEquipmentId())
                .orElseThrow(() -> new RuntimeException("Equipment not found"));

        if (!equipment.isAvailable()) {
            throw new RuntimeException("Equipment is currently not available for booking.");
        }

        // NEW: 3. Check for specific Date Overlaps!
        boolean isOverlapping = bookingRepository.hasOverlappingBookings(
                bookingDTO.getEquipmentId(),
                bookingDTO.getStartDate(),
                bookingDTO.getEndDate()
        );

        if (isOverlapping) {
            throw new RuntimeException("Equipment is already booked during these dates. Please select different dates.");
        }
        UserResponseDTO renter;
        try {
            renter = userClient.getUserById(bookingDTO.getRenterId());
        } catch (Exception e) {
            log.warn("User service unavailable while verifying renter: {}", e.getMessage());
            throw new RuntimeException("Unable to verify your account. Please try again later.");
        }
        if (renter == null) {
            throw new RuntimeException("Renter profile not found in the system.");
        }

        // Prevent owners from renting their own equipment
        if (equipment.getOwnerId().equals(bookingDTO.getRenterId())) {
            throw new RuntimeException("You cannot book your own equipment.");
        }

        // 4. Calculate total cost
        BigDecimal totalCost = equipment.getDailyRate().multiply(BigDecimal.valueOf(totalDays));

        // 5. Build and save the booking
        Booking booking = Booking.builder()
                .equipmentId(equipment.getId())
                .renterId(renter.getUserId())
                .startDate(bookingDTO.getStartDate())
                .endDate(bookingDTO.getEndDate())
                .totalCost(totalCost)
                .status(Status.REQUESTED) // Initial state
                .build();

        BookingDTO saved = mapToDTO(bookingRepository.save(booking));

        try {
            notificationService.create(
                    equipment.getOwnerId(),
                    "BOOKING_REQUESTED",
                    renter.getFullName() + " requested to rent " + equipment.getName() + " from " + bookingDTO.getStartDate() + " to " + bookingDTO.getEndDate(),
                    booking.getId()
            );
        } catch (Exception e) {
            log.warn("Failed to create notification: {}", e.getMessage());
        }

        return saved;
    }

    @Override
    public BookingDTO getBookingById(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        return mapToDTO(booking);
    }

    @Override
    public List<BookingDTO> getBookingsByRenter(String renterId) {
        return bookingRepository.findByRenterId(renterId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDTO> getBookingsByEquipment(String equipmentId) {
        return bookingRepository.findByEquipmentId(equipmentId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookingDTO updateBookingStatus(String bookingId, Status status) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        Equipment equipment = equipmentRepository.findById(booking.getEquipmentId())
                .orElse(null);

        booking.setStatus(status);
        BookingDTO updated = mapToDTO(bookingRepository.save(booking));

        try {
            String msg;
            if (status == Status.CONFIRMED) {
                msg = "Your booking request for " + (equipment != null ? equipment.getName() : "equipment") + " has been confirmed!";
            } else if (status == Status.COMPLETED) {
                msg = "Your rental of " + (equipment != null ? equipment.getName() : "equipment") + " has been marked as completed.";
            } else {
                msg = "Your booking for " + (equipment != null ? equipment.getName() : "equipment") + " has been " + status.name().toLowerCase() + ".";
            }
            notificationService.create(booking.getRenterId(), "BOOKING_" + status.name(), msg, bookingId);
        } catch (Exception e) {
            log.warn("Failed to create notification: {}", e.getMessage());
        }

        return updated;
    }

    @Override
    @Transactional
    public void deleteBookingsByRenter(String renterId) {
        List<Booking> bookings = bookingRepository.findByRenterId(renterId);
        for (Booking b : bookings) {
            notificationRepository.deleteByRelatedId(b.getId());
        }
        bookingRepository.deleteByRenterId(renterId);
    }

    @Override
    @Transactional
    public BookingDTO cancelBookingByRenter(String bookingId, String renterId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getRenterId().equals(renterId)) {
            throw new RuntimeException("You can only cancel your own bookings.");
        }
        if (booking.getStatus() != Status.REQUESTED) {
            throw new RuntimeException("Only REQUESTED bookings can be cancelled.");
        }

        Equipment equipment = equipmentRepository.findById(booking.getEquipmentId()).orElse(null);

        booking.setStatus(Status.CANCELLED);
        BookingDTO updated = mapToDTO(bookingRepository.save(booking));

        try {
            if (equipment != null) {
                notificationService.create(
                        equipment.getOwnerId(),
                        "BOOKING_CANCELLED",
                        "A booking for " + equipment.getName() + " has been cancelled by the renter.",
                        bookingId
                );
            }
        } catch (Exception e) {
            log.warn("Failed to create notification: {}", e.getMessage());
        }

        return updated;
    }

 private BookingDTO mapToDTO(Booking booking){
        return BookingDTO.builder()
                .bookingId(booking.getId())
                .equipmentId(booking.getEquipmentId())
                .renterId(booking.getRenterId())
                .startDate(booking.getStartDate())
                .endDate(booking.getEndDate())
                .totalCost(booking.getTotalCost())
                .status(booking.getStatus())
                .build();
 }
}