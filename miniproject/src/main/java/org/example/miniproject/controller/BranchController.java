package org.example.miniproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.miniproject.dto.BranchRequest;
import org.example.miniproject.entity.Branch;
import org.example.miniproject.service.BranchService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/branches")
@RequiredArgsConstructor
public class BranchController {

    private final BranchService branchService;

    @GetMapping
    public List<Branch> listBranches() {
        return branchService.findAll();
    }

    @GetMapping("/{id}")
    public Branch getBranch(@PathVariable Long id) {
        return branchService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Branch createBranch(@Valid @RequestBody BranchRequest request) {
        return branchService.create(request);
    }
}
