package com.kisan.marketplace.service;

import com.kisan.marketplace.dto.BookingDTO;
import com.kisan.marketplace.enums.Status;

import java.util.List;

public interface BookingService {
    BookingDTO createBooking(BookingDTO bookingDTO);
    BookingDTO getBookingById(String bookingId);
    List<BookingDTO> getBookingsByRenter(String renterId);
    List<BookingDTO> getBookingsByEquipment(String equipmentId);
    BookingDTO updateBookingStatus(String bookingId, Status status);
    BookingDTO cancelBookingByRenter(String bookingId, String renterId);

    void deleteBookingsByRenter(String renterId);
}
