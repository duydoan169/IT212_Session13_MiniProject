package org.example.miniproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RevenueReportResponse {
    private long totalInvoices;
    private long paidInvoices;
    private long unpaidInvoices;
    private double totalRevenue;
}
