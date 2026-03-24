package com.kisan.marketplace.controller.impl;

import com.kisan.marketplace.controller.BookingController;
import com.kisan.marketplace.dto.BookingDTO;
import com.kisan.marketplace.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RequiredArgsConstructor
public class BookingControllerImpl implements BookingController {

    @Autowired
    private final BookingService bookingService;


    public ResponseEntity<BookingDTO> creatingBooking(BookingDTO bookingDTO){
        return new ResponseEntity<>(bookingService.createBooking(bookingDTO), HttpStatus.CREATED);
    }

    public ResponseEntity<BookingDTO> getBooking(String id) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    public ResponseEntity<List<BookingDTO>> getBookingsByRenter(String renterId) {
        return ResponseEntity.ok(bookingService.getBookingsByRenter(renterId));
    }

    public ResponseEntity<List<BookingDTO>> getBookingsByEquipment(String equipmentId) {
        return ResponseEntity.ok(bookingService.getBookingsByEquipment(equipmentId));
    }

    public ResponseEntity<BookingDTO> updateBookingStatus(String id, String status) {
        return ResponseEntity.ok(bookingService.updateBookingStatus(id, status));
    }
}
