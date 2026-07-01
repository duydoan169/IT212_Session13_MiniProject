package org.example.miniproject.dto;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class MenuItemUpdateRequest {
    private String name;
    private String category;

    @Positive(message = "price must be positive")
    private Double price;

    private Boolean isAvailable;
}
