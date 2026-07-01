package org.example.miniproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.miniproject.dto.BookingRequest;
import org.example.miniproject.dto.BookingUpdateRequest;
import org.example.miniproject.entity.Booking;
import org.example.miniproject.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping
    public List<Booking> listBookings(@RequestParam(required = false) String status,
                                       @RequestParam(required = false) Long customerId,
                                       @RequestParam(required = false) Long roomId) {
        return bookingService.findAll(status, customerId, roomId);
    }

    @GetMapping("/{id}")
    public Booking getBooking(@PathVariable Long id) {
        return bookingService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Booking createBooking(@Valid @RequestBody BookingRequest request) {
        return bookingService.create(request);
    }

    @PutMapping("/{id}")
    public Booking updateBooking(@PathVariable Long id, @RequestBody BookingUpdateRequest request) {
        return bookingService.update(id, request);
    }

    @PostMapping("/{id}/checkin")
    public Booking checkIn(@PathVariable Long id) {
        return bookingService.checkIn(id);
    }

    @PostMapping("/{id}/checkout")
    public Booking checkOut(@PathVariable Long id) {
        return bookingService.checkOut(id);
    }
}
