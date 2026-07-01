package org.example.miniproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.miniproject.dto.MenuItemRequest;
import org.example.miniproject.dto.MenuItemUpdateRequest;
import org.example.miniproject.entity.MenuItem;
import org.example.miniproject.service.MenuItemService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu-items")
@RequiredArgsConstructor
public class MenuItemController {

    private final MenuItemService menuItemService;

    @GetMapping
    public List<MenuItem> listMenuItems(@RequestParam(required = false) String category,
                                         @RequestParam(required = false) Boolean available) {
        return menuItemService.findAll(category, available);
    }

    @GetMapping("/{id}")
    public MenuItem getMenuItem(@PathVariable Long id) {
        return menuItemService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MenuItem createMenuItem(@Valid @RequestBody MenuItemRequest request) {
        return menuItemService.create(request);
    }

    @PutMapping("/{id}")
    public MenuItem updateMenuItem(@PathVariable Long id, @Valid @RequestBody MenuItemUpdateRequest request) {
        return menuItemService.update(id, request);
    }
}
