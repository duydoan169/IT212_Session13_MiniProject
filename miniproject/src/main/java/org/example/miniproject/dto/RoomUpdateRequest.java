package org.example.miniproject.dto;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class RoomUpdateRequest {
    private String roomNumber;
    private String roomType;

    @Positive(message = "pricePerHour must be positive")
    private Double pricePerHour;

    private String status;
}
