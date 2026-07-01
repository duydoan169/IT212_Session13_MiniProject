package org.example.miniproject.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class MenuItemRequest {

    @NotNull(message = "name is required")
    private String name;

    @NotNull(message = "category is required")
    private String category;

    @NotNull(message = "price is required")
    @Positive(message = "price must be positive")
    private Double price;
}
