package org.example.miniproject.service;

import lombok.RequiredArgsConstructor;
import org.example.miniproject.dto.OrderItemRequest;
import org.example.miniproject.dto.OrderItemUpdateRequest;
import org.example.miniproject.entity.Booking;
import org.example.miniproject.entity.BookingStatus;
import org.example.miniproject.entity.MenuItem;
import org.example.miniproject.entity.OrderItem;
import org.example.miniproject.exception.BadRequestException;
import org.example.miniproject.exception.ResourceNotFoundException;
import org.example.miniproject.repository.BookingRepository;
import org.example.miniproject.repository.MenuItemRepository;
import org.example.miniproject.repository.OrderItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final BookingRepository bookingRepository;
    private final MenuItemRepository menuItemRepository;

    public List<OrderItem> findByBooking(Long bookingId) {
        return orderItemRepository.findByBooking_BookingId(bookingId);
    }

    public OrderItem addOrderItem(Long bookingId, OrderItemRequest request) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id " + bookingId));

        if (booking.getStatus() != BookingStatus.CHECKED_IN) {
            throw new BadRequestException("Can only add orders to a booking that is CHECKED_IN");
        }

        MenuItem menuItem = menuItemRepository.findById(request.getItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id " + request.getItemId()));

        if (!Boolean.TRUE.equals(menuItem.getIsAvailable())) {
            throw new BadRequestException("Menu item is not available");
        }

        OrderItem orderItem = new OrderItem();
        orderItem.setBooking(booking);
        orderItem.setMenuItem(menuItem);
        orderItem.setQuantity(request.getQuantity());
        orderItem.setUnitPrice(menuItem.getPrice());
        return orderItemRepository.save(orderItem);
    }

    public OrderItem updateOrderItem(Long orderItemId, OrderItemUpdateRequest request) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Order item not found with id " + orderItemId));
        orderItem.setQuantity(request.getQuantity());
        return orderItemRepository.save(orderItem);
    }

    public void deleteOrderItem(Long orderItemId) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Order item not found with id " + orderItemId));
        orderItemRepository.delete(orderItem);
    }
}
