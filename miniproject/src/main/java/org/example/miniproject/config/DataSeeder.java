package org.example.miniproject.config;

import lombok.RequiredArgsConstructor;
import org.example.miniproject.entity.*;
import org.example.miniproject.repository.BranchRepository;
import org.example.miniproject.repository.MenuItemRepository;
import org.example.miniproject.repository.RoomRepository;
import org.example.miniproject.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final BranchRepository branchRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final MenuItemRepository menuItemRepository;

    @Override
    public void run(String... args) {
        if (branchRepository.count() > 0) {
            return; // already seeded
        }

        Branch branch = new Branch();
        branch.setName("Karaoke Star - District 1");
        branch.setAddress("123 Le Loi, Q1, HCMC");
        branch.setPhone("0281234567");
        branch = branchRepository.save(branch);

        User manager = new User();
        manager.setBranch(branch);
        manager.setFullName("Nguyen Van Quan Ly");
        manager.setUsername("manager1");
        manager.setPassword("manager123");
        manager.setRole(UserRole.MANAGER);
        userRepository.save(manager);

        User receptionist = new User();
        receptionist.setBranch(branch);
        receptionist.setFullName("Tran Thi Le Tan");
        receptionist.setUsername("reception1");
        receptionist.setPassword("reception123");
        receptionist.setRole(UserRole.RECEPTIONIST);
        userRepository.save(receptionist);

        Room room1 = new Room();
        room1.setBranch(branch);
        room1.setRoomNumber("R101");
        room1.setRoomType("Small");
        room1.setPricePerHour(100000.0);
        room1.setStatus(RoomStatus.AVAILABLE);
        roomRepository.save(room1);

        Room room2 = new Room();
        room2.setBranch(branch);
        room2.setRoomNumber("R102");
        room2.setRoomType("Medium");
        room2.setPricePerHour(150000.0);
        room2.setStatus(RoomStatus.AVAILABLE);
        roomRepository.save(room2);

        Room room3 = new Room();
        room3.setBranch(branch);
        room3.setRoomNumber("R201");
        room3.setRoomType("VIP");
        room3.setPricePerHour(300000.0);
        room3.setStatus(RoomStatus.AVAILABLE);
        roomRepository.save(room3);

        menuItemRepository.save(newItem("Coca Cola", "Drink", 20000.0));
        menuItemRepository.save(newItem("Beer Tiger", "Drink", 25000.0));
        menuItemRepository.save(newItem("Fried Squid", "Food", 90000.0));
        menuItemRepository.save(newItem("Fruit Platter", "Food", 120000.0));
    }

    private MenuItem newItem(String name, String category, Double price) {
        MenuItem item = new MenuItem();
        item.setName(name);
        item.setCategory(category);
        item.setPrice(price);
        item.setIsAvailable(true);
        return item;
    }
}
