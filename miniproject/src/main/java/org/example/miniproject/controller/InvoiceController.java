package org.example.miniproject.controller;

import lombok.RequiredArgsConstructor;
import org.example.miniproject.dto.RevenueReportResponse;
import org.example.miniproject.entity.Invoice;
import org.example.miniproject.service.InvoiceService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping("/api/bookings/{bookingId}/invoice")
    @ResponseStatus(HttpStatus.CREATED)
    public Invoice generateInvoice(@PathVariable Long bookingId,
                                    @RequestParam(required = false) Long issuedByUserId) {
        return invoiceService.generateInvoice(bookingId, issuedByUserId);
    }

    @GetMapping("/api/invoices/{id}")
    public Invoice getInvoice(@PathVariable Long id) {
        return invoiceService.findById(id);
    }

    @PutMapping("/api/invoices/{id}/pay")
    public Invoice payInvoice(@PathVariable Long id) {
        return invoiceService.pay(id);
    }

    @GetMapping("/api/reports/revenue")
    public RevenueReportResponse revenueReport() {
        return invoiceService.revenueReport();
    }
}
