package org.example.miniproject.repository;

import org.example.miniproject.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByBooking_BookingId(Long bookingId);
}
