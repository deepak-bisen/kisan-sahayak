package com.kisan.marketplace.service;

import com.kisan.marketplace.dto.BookingDTO;

import java.util.List;

public interface BookingService {
    BookingDTO createBooking(BookingDTO bookingDTO);
    BookingDTO getBookingById(String bookingId);
    List<BookingDTO> getBookingsByRenter(String renterId);
    List<BookingDTO> getBookingsByEquipment(String equipmentId);
    BookingDTO updateBookingStatus(String bookingId, String status);
}
