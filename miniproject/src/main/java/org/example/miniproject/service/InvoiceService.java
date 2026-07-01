package org.example.miniproject.service;

import lombok.RequiredArgsConstructor;
import org.example.miniproject.dto.RevenueReportResponse;
import org.example.miniproject.entity.*;
import org.example.miniproject.exception.BadRequestException;
import org.example.miniproject.exception.ResourceNotFoundException;
import org.example.miniproject.repository.BookingRepository;
import org.example.miniproject.repository.InvoiceRepository;
import org.example.miniproject.repository.OrderItemRepository;
import org.example.miniproject.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final BookingRepository bookingRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;

    @Transactional
    public Invoice generateInvoice(Long bookingId, Long issuedByUserId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id " + bookingId));

        if (booking.getStatus() != BookingStatus.COMPLETED) {
            throw new BadRequestException("Invoice can only be generated for a COMPLETED booking");
        }

        if (invoiceRepository.findByBooking_BookingId(bookingId).isPresent()) {
            throw new BadRequestException("Invoice already exists for this booking");
        }

        double hours = Duration.between(booking.getStartTime(), booking.getEndTime()).toMinutes() / 60.0;
        double roomFee = hours * booking.getRoom().getPricePerHour();

        List<OrderItem> orderItems = orderItemRepository.findByBooking_BookingId(bookingId);
        double orderFee = orderItems.stream()
                .mapToDouble(oi -> oi.getUnitPrice() * oi.getQuantity())
                .sum();

        User issuedBy = null;
        if (issuedByUserId != null) {
            issuedBy = userRepository.findById(issuedByUserId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + issuedByUserId));
        }

        Invoice invoice = new Invoice();
        invoice.setBooking(booking);
        invoice.setIssuedBy(issuedBy);
        invoice.setRoomFee(roomFee);
        invoice.setOrderFee(orderFee);
        invoice.setTotalAmount(roomFee + orderFee);
        invoice.setStatus(InvoiceStatus.UNPAID);

        return invoiceRepository.save(invoice);
    }

    public Invoice findById(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id " + id));
    }

    public Invoice pay(Long id) {
        Invoice invoice = findById(id);
        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new BadRequestException("Invoice is already paid");
        }
        invoice.setStatus(InvoiceStatus.PAID);
        return invoiceRepository.save(invoice);
    }

    public RevenueReportResponse revenueReport() {
        List<Invoice> invoices = invoiceRepository.findAll();
        long paid = invoices.stream().filter(i -> i.getStatus() == InvoiceStatus.PAID).count();
        double totalRevenue = invoices.stream()
                .filter(i -> i.getStatus() == InvoiceStatus.PAID)
                .mapToDouble(Invoice::getTotalAmount)
                .sum();
        return new RevenueReportResponse(invoices.size(), paid, invoices.size() - paid, totalRevenue);
    }
}
