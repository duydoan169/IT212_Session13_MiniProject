package org.example.miniproject.repository;

import org.example.miniproject.entity.Booking;
import org.example.miniproject.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByStatus(BookingStatus status);
    List<Booking> findByCustomer_CustomerId(Long customerId);
    List<Booking> findByRoom_RoomId(Long roomId);

    List<Booking> findByRoom_RoomIdAndStatusNotInAndStartTimeLessThanAndEndTimeGreaterThan(
            Long roomId, List<BookingStatus> excludedStatuses, LocalDateTime end, LocalDateTime start);
}
