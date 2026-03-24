package com.kisan.marketplace.service.impl;

import com.kisan.marketplace.client.UserClient;
import com.kisan.marketplace.dto.BookingDTO;
import com.kisan.marketplace.dto.UserResponseDTO;
import com.kisan.marketplace.entity.Booking;
import com.kisan.marketplace.entity.Equipment;
import com.kisan.marketplace.repository.BookingRepository;
import com.kisan.marketplace.repository.EquipmentRepository;
import com.kisan.marketplace.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final EquipmentRepository equipmentRepository;
    private final UserClient userClient;

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

        // 3. Verify Renter exists via User Service (Feign Client)
        UserResponseDTO renter = userClient.getUserById(bookingDTO.getRenterId());
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
                .equipmentId(equipment.getEquipmentId())
                .renterId(renter.getUserId())
                .startDate(bookingDTO.getStartDate())
                .endDate(bookingDTO.getEndDate())
                .totalCost(totalCost)
                .status("REQUESTED") // Initial state
                .build();

        return mapToDTO(bookingRepository.save(booking));
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
    public BookingDTO updateBookingStatus(String bookingId, String status) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Allowable statuses: REQUESTED, CONFIRMED, COMPLETED, CANCELLED
        if (!status.matches("^(REQUESTED|CONFIRMED|COMPLETED|CANCELLED)$")) {
            throw new RuntimeException("Invalid status update.");
        }

        booking.setStatus(status);
        return mapToDTO(bookingRepository.save(booking));
    }

 private BookingDTO mapToDTO(Booking booking){
        return BookingDTO.builder()
                .bookingId(booking.getBookingId())
                .equipmentId(booking.getEquipmentId())
                .renterId(booking.getRenterId())
                .startDate(booking.getStartDate())
                .endDate(booking.getEndDate())
                .totalCost(booking.getTotalCost())
                .status(booking.getStatus())
                .build();
 }
}