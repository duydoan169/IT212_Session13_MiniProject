package org.example.miniproject.service;

import lombok.RequiredArgsConstructor;
import org.example.miniproject.dto.BranchRequest;
import org.example.miniproject.entity.Branch;
import org.example.miniproject.exception.ResourceNotFoundException;
import org.example.miniproject.repository.BranchRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BranchService {

    private final BranchRepository branchRepository;

    public List<Branch> findAll() {
        return branchRepository.findAll();
    }

    public Branch findById(Long id) {
        return branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found with id " + id));
    }

    public Branch create(BranchRequest request) {
        Branch branch = new Branch();
        branch.setName(request.getName());
        branch.setAddress(request.getAddress());
        branch.setPhone(request.getPhone());
        return branchRepository.save(branch);
    }
}
