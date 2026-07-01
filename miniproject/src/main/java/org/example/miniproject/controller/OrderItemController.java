package org.example.miniproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.miniproject.dto.OrderItemRequest;
import org.example.miniproject.dto.OrderItemUpdateRequest;
import org.example.miniproject.entity.OrderItem;
import org.example.miniproject.service.OrderItemService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderItemController {

    private final OrderItemService orderItemService;

    @GetMapping("/api/bookings/{bookingId}/orders")
    public List<OrderItem> listOrderItems(@PathVariable Long bookingId) {
        return orderItemService.findByBooking(bookingId);
    }

    @PostMapping("/api/bookings/{bookingId}/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderItem addOrderItem(@PathVariable Long bookingId, @Valid @RequestBody OrderItemRequest request) {
        return orderItemService.addOrderItem(bookingId, request);
    }

    @PutMapping("/api/orders/{orderItemId}")
    public OrderItem updateOrderItem(@PathVariable Long orderItemId, @Valid @RequestBody OrderItemUpdateRequest request) {
        return orderItemService.updateOrderItem(orderItemId, request);
    }

    @DeleteMapping("/api/orders/{orderItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrderItem(@PathVariable Long orderItemId) {
        orderItemService.deleteOrderItem(orderItemId);
    }
}
