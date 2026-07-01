package org.example.miniproject.service;

import lombok.RequiredArgsConstructor;
import org.example.miniproject.dto.UserRequest;
import org.example.miniproject.entity.Branch;
import org.example.miniproject.entity.User;
import org.example.miniproject.entity.UserRole;
import org.example.miniproject.exception.BadRequestException;
import org.example.miniproject.exception.ResourceNotFoundException;
import org.example.miniproject.repository.BranchRepository;
import org.example.miniproject.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BranchRepository branchRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
    }

    public User create(UserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("username already exists");
        }
        UserRole role;
        try {
            role = UserRole.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("role must be RECEPTIONIST or MANAGER");
        }
        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found with id " + request.getBranchId()));

        User user = new User();
        user.setBranch(branch);
        user.setFullName(request.getFullName());
        user.setUsername(request.getUsername());
        // NOTE: kept as plain text for simplicity of this mini project (no Spring Security dependency requested).
        // In a production system this must be hashed (e.g. BCrypt).
        user.setPassword(request.getPassword());
        user.setRole(role);
        return userRepository.save(user);
    }
}
