package org.example.miniproject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.miniproject.repository.MenuItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MenuItemControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private MenuItemRepository menuItemRepository;

    @BeforeEach
    void setUp() {
        menuItemRepository.deleteAll();
    }

    @Test
    void createMenuItem_success() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("name", "Beer Tiger");
        payload.put("category", "Drink");
        payload.put("price", 25000.0);

        mockMvc.perform(post("/api/menu-items")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.isAvailable").value(true));
    }

    @Test
    void createMenuItem_missingName_returnsBadRequest() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("category", "Drink");
        payload.put("price", 25000.0);

        mockMvc.perform(post("/api/menu-items")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateMenuItem_toggleAvailability() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("name", "Fried Squid");
        payload.put("category", "Food");
        payload.put("price", 90000.0);

        String response = mockMvc.perform(post("/api/menu-items")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(payload)))
                .andReturn().getResponse().getContentAsString();
        Long id = objectMapper.readTree(response).get("itemId").asLong();

        Map<String, Object> update = new HashMap<>();
        update.put("isAvailable", false);

        mockMvc.perform(put("/api/menu-items/" + id)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isAvailable").value(false));
    }
}
