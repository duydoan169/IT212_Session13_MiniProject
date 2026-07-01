package org.example.miniproject.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingUpdateRequest {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
}
