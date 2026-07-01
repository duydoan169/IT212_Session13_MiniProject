package org.example.miniproject.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingRequest {

    @NotNull(message = "roomId is required")
    private Long roomId;

    private Long customerId;

    private String customerName;

    private String customerPhone;

    @NotNull(message = "startTime is required")
    private LocalDateTime startTime;

    @NotNull(message = "endTime is required")
    private LocalDateTime endTime;

    private Long userId;
}
