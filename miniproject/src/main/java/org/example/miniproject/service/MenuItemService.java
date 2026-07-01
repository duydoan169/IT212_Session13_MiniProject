package org.example.miniproject.service;

import lombok.RequiredArgsConstructor;
import org.example.miniproject.dto.MenuItemRequest;
import org.example.miniproject.dto.MenuItemUpdateRequest;
import org.example.miniproject.entity.MenuItem;
import org.example.miniproject.exception.ResourceNotFoundException;
import org.example.miniproject.repository.MenuItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;

    public List<MenuItem> findAll(String category, Boolean available) {
        if (category != null) return menuItemRepository.findByCategory(category);
        if (available != null) return menuItemRepository.findByIsAvailable(available);
        return menuItemRepository.findAll();
    }

    public MenuItem findById(Long id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id " + id));
    }

    public MenuItem create(MenuItemRequest request) {
        MenuItem item = new MenuItem();
        item.setName(request.getName());
        item.setCategory(request.getCategory());
        item.setPrice(request.getPrice());
        item.setIsAvailable(true);
        return menuItemRepository.save(item);
    }

    public MenuItem update(Long id, MenuItemUpdateRequest request) {
        MenuItem item = findById(id);
        if (request.getName() != null) item.setName(request.getName());
        if (request.getCategory() != null) item.setCategory(request.getCategory());
        if (request.getPrice() != null) item.setPrice(request.getPrice());
        if (request.getIsAvailable() != null) item.setIsAvailable(request.getIsAvailable());
        return menuItemRepository.save(item);
    }
}
