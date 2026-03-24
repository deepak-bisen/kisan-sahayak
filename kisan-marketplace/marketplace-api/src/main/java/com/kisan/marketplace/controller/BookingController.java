package com.kisan.marketplace.controller;

import com.kisan.marketplace.dto.BookingDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public interface BookingController {

    @PostMapping
    public ResponseEntity<BookingDTO> creatingBooking(@Valid @RequestBody BookingDTO bookingDTO);

    @GetMapping("/{id}")
    public ResponseEntity<BookingDTO> getBooking(@PathVariable String id);

    @GetMapping("/renter/{renterId}")
    public ResponseEntity<List<BookingDTO>> getBookingsByRenter(@PathVariable String renterId);

    @GetMapping("/equipment/{equipmentId}")
    public ResponseEntity<List<BookingDTO>> getBookingsByEquipment(@PathVariable String equipmentId);

    @PatchMapping("/{id}/status")
    public ResponseEntity<BookingDTO> updateBookingStatus(
            @PathVariable String id,
            @RequestParam String status);
}
