package org.example.miniproject.service;

import lombok.RequiredArgsConstructor;
import org.example.miniproject.dto.BookingRequest;
import org.example.miniproject.dto.BookingUpdateRequest;
import org.example.miniproject.entity.*;
import org.example.miniproject.exception.BadRequestException;
import org.example.miniproject.exception.ConflictException;
import org.example.miniproject.exception.ResourceNotFoundException;
import org.example.miniproject.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    private static final List<BookingStatus> INACTIVE_STATUSES = List.of(BookingStatus.CANCELLED, BookingStatus.COMPLETED);

    public List<Booking> findAll(String status, Long customerId, Long roomId) {
        if (status != null) return bookingRepository.findByStatus(parseStatus(status));
        if (customerId != null) return bookingRepository.findByCustomer_CustomerId(customerId);
        if (roomId != null) return bookingRepository.findByRoom_RoomId(roomId);
        return bookingRepository.findAll();
    }

    public Booking findById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id " + id));
    }

    @Transactional
    public Booking create(BookingRequest request) {
        if (!request.getStartTime().isBefore(request.getEndTime())) {
            throw new BadRequestException("startTime must be before endTime");
        }

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id " + request.getRoomId()));

        ensureRoomAvailable(room.getRoomId(), request.getStartTime(), request.getEndTime(), null);

        Customer customer;
        if (request.getCustomerId() != null) {
            customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id " + request.getCustomerId()));
        } else if (request.getCustomerName() != null && !request.getCustomerName().isBlank()) {
            Customer newCustomer = new Customer();
            newCustomer.setFullName(request.getCustomerName());
            newCustomer.setPhone(request.getCustomerPhone());
            customer = customerRepository.save(newCustomer);
        } else {
            throw new BadRequestException("customerId or customerName is required");
        }

        User user = null;
        if (request.getUserId() != null) {
            user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + request.getUserId()));
        }

        Booking booking = new Booking();
        booking.setRoom(room);
        booking.setCustomer(customer);
        booking.setUser(user);
        booking.setStartTime(request.getStartTime());
        booking.setEndTime(request.getEndTime());
        booking.setStatus(BookingStatus.CONFIRMED);

        Booking saved = bookingRepository.save(booking);

        room.setStatus(RoomStatus.BOOKED);
        roomRepository.save(room);

        return saved;
    }

    @Transactional
    public Booking update(Long id, BookingUpdateRequest request) {
        Booking booking = findById(id);

        if (request.getStartTime() != null && request.getEndTime() != null) {
            if (!request.getStartTime().isBefore(request.getEndTime())) {
                throw new BadRequestException("startTime must be before endTime");
            }
            ensureRoomAvailable(booking.getRoom().getRoomId(), request.getStartTime(), request.getEndTime(), booking.getBookingId());
            booking.setStartTime(request.getStartTime());
            booking.setEndTime(request.getEndTime());
        }

        if (request.getStatus() != null) {
            BookingStatus newStatus = parseStatus(request.getStatus());
            booking.setStatus(newStatus);
            if (newStatus == BookingStatus.CANCELLED) {
                Room room = booking.getRoom();
                room.setStatus(RoomStatus.AVAILABLE);
                roomRepository.save(room);
            }
        }

        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking checkIn(Long id) {
        Booking booking = findById(id);
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new BadRequestException("Cannot check-in a booking with status " + booking.getStatus());
        }
        booking.setStatus(BookingStatus.CHECKED_IN);
        Room room = booking.getRoom();
        room.setStatus(RoomStatus.IN_USE);
        roomRepository.save(room);
        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking checkOut(Long id) {
        Booking booking = findById(id);
        if (booking.getStatus() != BookingStatus.CHECKED_IN) {
            throw new BadRequestException("Cannot check-out a booking with status " + booking.getStatus());
        }
        booking.setStatus(BookingStatus.COMPLETED);
        Room room = booking.getRoom();
        room.setStatus(RoomStatus.CLEANING);
        roomRepository.save(room);
        return bookingRepository.save(booking);
    }

    private void ensureRoomAvailable(Long roomId, java.time.LocalDateTime start, java.time.LocalDateTime end, Long excludeBookingId) {
        List<Booking> overlapping = bookingRepository
                .findByRoom_RoomIdAndStatusNotInAndStartTimeLessThanAndEndTimeGreaterThan(
                        roomId, INACTIVE_STATUSES, end, start);
        boolean conflict = overlapping.stream()
                .anyMatch(b -> excludeBookingId == null || !b.getBookingId().equals(excludeBookingId));
        if (conflict) {
            throw new ConflictException("Room is not available for the selected time range");
        }
    }

    private BookingStatus parseStatus(String status) {
        try {
            return BookingStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("status must be one of PENDING, CONFIRMED, CHECKED_IN, COMPLETED, CANCELLED");
        }
    }
}
