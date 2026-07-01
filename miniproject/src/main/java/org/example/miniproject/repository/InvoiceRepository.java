package org.example.miniproject.repository;

import org.example.miniproject.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByBooking_BookingId(Long bookingId);
}
