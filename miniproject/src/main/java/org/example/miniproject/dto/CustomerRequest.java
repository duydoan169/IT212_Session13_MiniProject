package org.example.miniproject.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CustomerRequest {

    @NotNull(message = "fullName is required")
    private String fullName;

    private String phone;
    private String email;
}
