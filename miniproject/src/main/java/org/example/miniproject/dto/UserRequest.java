package org.example.miniproject.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserRequest {

    @NotNull(message = "branchId is required")
    private Long branchId;

    @NotNull(message = "fullName is required")
    private String fullName;

    @NotNull(message = "username is required")
    private String username;

    @NotNull(message = "password is required")
    private String password;

    @NotNull(message = "role is required")
    private String role;
}
