package org.example.miniproject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.miniproject.entity.Branch;
import org.example.miniproject.repository.BranchRepository;
import org.example.miniproject.repository.RoomRepository;
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
class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private RoomRepository roomRepository;

    private Long branchId;

    @BeforeEach
    void setUp() {
        roomRepository.deleteAll();
        branchRepository.deleteAll();
        Branch branch = new Branch();
        branch.setName("Test Branch");
        branch.setAddress("Test Address");
        branch.setPhone("0123456789");
        branchId = branchRepository.save(branch).getBranchId();
    }

    @Test
    void createRoom_success() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("branchId", branchId);
        payload.put("roomNumber", "R101");
        payload.put("roomType", "Small");
        payload.put("pricePerHour", 100000.0);

        mockMvc.perform(post("/api/rooms")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.roomNumber").value("R101"))
                .andExpect(jsonPath("$.status").value("AVAILABLE"));
    }

    @Test
    void createRoom_invalidPrice_returnsBadRequest() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("branchId", branchId);
        payload.put("roomNumber", "R101");
        payload.put("roomType", "Small");
        payload.put("pricePerHour", -50);

        mockMvc.perform(post("/api/rooms")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createRoom_unknownBranch_returnsNotFound() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("branchId", 999999L);
        payload.put("roomNumber", "R999");
        payload.put("roomType", "Small");
        payload.put("pricePerHour", 100000.0);

        mockMvc.perform(post("/api/rooms")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isNotFound());
    }

    @Test
    void listRooms_filterByBranch_returnsOnlyMatching() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("branchId", branchId);
        payload.put("roomNumber", "R102");
        payload.put("roomType", "Medium");
        payload.put("pricePerHour", 150000.0);
        mockMvc.perform(post("/api/rooms")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/rooms").param("branchId", String.valueOf(branchId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getRoom_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/rooms/999999"))
                .andExpect(status().isNotFound());
    }
}
