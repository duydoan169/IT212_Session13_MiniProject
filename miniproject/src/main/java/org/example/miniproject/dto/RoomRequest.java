package org.example.miniproject.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class RoomRequest {

    @NotNull(message = "branchId is required")
    private Long branchId;

    @NotNull(message = "roomNumber is required")
    private String roomNumber;

    @NotNull(message = "roomType is required")
    private String roomType;

    @NotNull(message = "pricePerHour is required")
    @Positive(message = "pricePerHour must be positive")
    private Double pricePerHour;
}
