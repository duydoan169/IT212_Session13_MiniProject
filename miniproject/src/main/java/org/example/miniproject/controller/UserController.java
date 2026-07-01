package org.example.miniproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.miniproject.dto.UserRequest;
import org.example.miniproject.entity.User;
import org.example.miniproject.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<User> listUsers() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@Valid @RequestBody UserRequest request) {
        return userService.create(request);
    }
}
