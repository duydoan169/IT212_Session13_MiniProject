package org.example.miniproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.miniproject.dto.RoomRequest;
import org.example.miniproject.dto.RoomUpdateRequest;
import org.example.miniproject.entity.Room;
import org.example.miniproject.service.RoomService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    public List<Room> listRooms(@RequestParam(required = false) Long branchId,
                                 @RequestParam(required = false) String status) {
        return roomService.findAll(branchId, status);
    }

    @GetMapping("/{id}")
    public Room getRoom(@PathVariable Long id) {
        return roomService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Room createRoom(@Valid @RequestBody RoomRequest request) {
        return roomService.create(request);
    }

    @PutMapping("/{id}")
    public Room updateRoom(@PathVariable Long id, @Valid @RequestBody RoomUpdateRequest request) {
        return roomService.update(id, request);
    }
}
