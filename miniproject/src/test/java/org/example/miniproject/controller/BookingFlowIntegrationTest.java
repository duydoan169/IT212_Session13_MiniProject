package org.example.miniproject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.miniproject.entity.*;
import org.example.miniproject.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BookingFlowIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Autowired private BranchRepository branchRepository;
    @Autowired private RoomRepository roomRepository;
    @Autowired private BookingRepository bookingRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private MenuItemRepository menuItemRepository;
    @Autowired private OrderItemRepository orderItemRepository;
    @Autowired private InvoiceRepository invoiceRepository;

    private Long roomId;
    private Long menuItemId;

    @BeforeEach
    void setUp() {
        invoiceRepository.deleteAll();
        orderItemRepository.deleteAll();
        bookingRepository.deleteAll();
        menuItemRepository.deleteAll();
        roomRepository.deleteAll();
        customerRepository.deleteAll();
        branchRepository.deleteAll();

        Branch branch = new Branch();
        branch.setName("Test Branch");
        branch = branchRepository.save(branch);

        Room room = new Room();
        room.setBranch(branch);
        room.setRoomNumber("R101");
        room.setRoomType("Small");
        room.setPricePerHour(100000.0);
        room.setStatus(RoomStatus.AVAILABLE);
        roomId = roomRepository.save(room).getRoomId();

        MenuItem item = new MenuItem();
        item.setName("Coca Cola");
        item.setCategory("Drink");
        item.setPrice(20000.0);
        item.setIsAvailable(true);
        menuItemId = menuItemRepository.save(item).getItemId();
    }

    @Test
    void fullBookingLifecycle_createCheckinOrderCheckoutInvoicePay() throws Exception {
        // 1. Create booking for a 2-hour slot
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = start.plusHours(2);

        Map<String, Object> bookingPayload = new HashMap<>();
        bookingPayload.put("roomId", roomId);
        bookingPayload.put("customerName", "Nguyen Van A");
        bookingPayload.put("customerPhone", "0909123456");
        bookingPayload.put("startTime", start.toString());
        bookingPayload.put("endTime", end.toString());

        String bookingResponse = mockMvc.perform(post("/api/bookings")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingPayload)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andReturn().getResponse().getContentAsString();

        Long bookingId = objectMapper.readTree(bookingResponse).get("bookingId").asLong();

        // room should now be BOOKED
        mockMvc.perform(get("/api/rooms/" + roomId))
                .andExpect(jsonPath("$.status").value("BOOKED"));

        // 2. Overlapping booking should be rejected with 409
        Map<String, Object> overlapPayload = new HashMap<>();
        overlapPayload.put("roomId", roomId);
        overlapPayload.put("customerName", "Nguyen Van B");
        overlapPayload.put("startTime", start.plusMinutes(30).toString());
        overlapPayload.put("endTime", end.plusMinutes(30).toString());
        mockMvc.perform(post("/api/bookings")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(overlapPayload)))
                .andExpect(status().isConflict());

        // 3. Check-in
        mockMvc.perform(post("/api/bookings/" + bookingId + "/checkin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CHECKED_IN"));

        // 4. Add an order item (2 x Coca Cola)
        Map<String, Object> orderPayload = new HashMap<>();
        orderPayload.put("itemId", menuItemId);
        orderPayload.put("quantity", 2);
        mockMvc.perform(post("/api/bookings/" + bookingId + "/orders")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(orderPayload)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.quantity").value(2));

        // 5. Check-out
        mockMvc.perform(post("/api/bookings/" + bookingId + "/checkout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        // 6. Generate invoice: expected roomFee = 2h * 100000 = 200000, orderFee = 2 * 20000 = 40000, total = 240000
        String invoiceResponse = mockMvc.perform(post("/api/bookings/" + bookingId + "/invoice"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.roomFee").value(200000.0))
                .andExpect(jsonPath("$.orderFee").value(40000.0))
                .andExpect(jsonPath("$.totalAmount").value(240000.0))
                .andExpect(jsonPath("$.status").value("UNPAID"))
                .andReturn().getResponse().getContentAsString();

        Long invoiceId = objectMapper.readTree(invoiceResponse).get("invoiceId").asLong();

        // 7. Pay invoice
        mockMvc.perform(put("/api/invoices/" + invoiceId + "/pay"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAID"));

        // 8. Revenue report should reflect the paid invoice
        mockMvc.perform(get("/api/reports/revenue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paidInvoices").value(1))
                .andExpect(jsonPath("$.totalRevenue").value(240000.0));
    }

    @Test
    void checkIn_onNonConfirmedBooking_returnsBadRequest() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = start.plusHours(1);
        Map<String, Object> bookingPayload = new HashMap<>();
        bookingPayload.put("roomId", roomId);
        bookingPayload.put("customerName", "Nguyen Van C");
        bookingPayload.put("startTime", start.toString());
        bookingPayload.put("endTime", end.toString());

        String bookingResponse = mockMvc.perform(post("/api/bookings")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingPayload)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long bookingId = objectMapper.readTree(bookingResponse).get("bookingId").asLong();

        // check-in once (success)
        mockMvc.perform(post("/api/bookings/" + bookingId + "/checkin")).andExpect(status().isOk());

        // check-in again should fail since status is now CHECKED_IN, not CONFIRMED
        mockMvc.perform(post("/api/bookings/" + bookingId + "/checkin")).andExpect(status().isBadRequest());
    }

    @Test
    void createBooking_startAfterEnd_returnsBadRequest() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusHours(2);
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        Map<String, Object> bookingPayload = new HashMap<>();
        bookingPayload.put("roomId", roomId);
        bookingPayload.put("customerName", "Nguyen Van D");
        bookingPayload.put("startTime", start.toString());
        bookingPayload.put("endTime", end.toString());

        mockMvc.perform(post("/api/bookings")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingPayload)))
                .andExpect(status().isBadRequest());
    }
}
