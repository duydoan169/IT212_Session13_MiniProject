package org.example.miniproject.service;

import lombok.RequiredArgsConstructor;
import org.example.miniproject.dto.RoomRequest;
import org.example.miniproject.dto.RoomUpdateRequest;
import org.example.miniproject.entity.Branch;
import org.example.miniproject.entity.Room;
import org.example.miniproject.entity.RoomStatus;
import org.example.miniproject.exception.BadRequestException;
import org.example.miniproject.exception.ResourceNotFoundException;
import org.example.miniproject.repository.BranchRepository;
import org.example.miniproject.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final BranchRepository branchRepository;

    public List<Room> findAll(Long branchId, String status) {
        if (branchId != null && status != null) {
            return roomRepository.findByBranch_BranchIdAndStatus(branchId, parseStatus(status));
        }
        if (branchId != null) {
            return roomRepository.findByBranch_BranchId(branchId);
        }
        if (status != null) {
            return roomRepository.findByStatus(parseStatus(status));
        }
        return roomRepository.findAll();
    }

    public Room findById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id " + id));
    }

    public Room create(RoomRequest request) {
        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found with id " + request.getBranchId()));

        Room room = new Room();
        room.setBranch(branch);
        room.setRoomNumber(request.getRoomNumber());
        room.setRoomType(request.getRoomType());
        room.setPricePerHour(request.getPricePerHour());
        room.setStatus(RoomStatus.AVAILABLE);
        return roomRepository.save(room);
    }

    public Room update(Long id, RoomUpdateRequest request) {
        Room room = findById(id);
        if (request.getRoomNumber() != null) room.setRoomNumber(request.getRoomNumber());
        if (request.getRoomType() != null) room.setRoomType(request.getRoomType());
        if (request.getPricePerHour() != null) room.setPricePerHour(request.getPricePerHour());
        if (request.getStatus() != null) room.setStatus(parseStatus(request.getStatus()));
        return roomRepository.save(room);
    }

    private RoomStatus parseStatus(String status) {
        try {
            return RoomStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("status must be one of AVAILABLE, BOOKED, IN_USE, CLEANING");
        }
    }
}
