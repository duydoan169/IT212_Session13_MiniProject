package org.example.miniproject.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BranchRequest {

    @NotNull(message = "name is required")
    private String name;

    private String address;
    private String phone;
}
