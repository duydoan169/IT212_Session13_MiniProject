package org.example.miniproject.repository;

import org.example.miniproject.entity.Room;
import org.example.miniproject.entity.RoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByBranch_BranchId(Long branchId);
    List<Room> findByStatus(RoomStatus status);
    List<Room> findByBranch_BranchIdAndStatus(Long branchId, RoomStatus status);
}
