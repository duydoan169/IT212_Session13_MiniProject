package org.example.miniproject.service;

import lombok.RequiredArgsConstructor;
import org.example.miniproject.dto.CustomerRequest;
import org.example.miniproject.entity.Customer;
import org.example.miniproject.exception.ResourceNotFoundException;
import org.example.miniproject.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    public Customer findById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id " + id));
    }

    public Customer create(CustomerRequest request) {
        Customer customer = new Customer();
        customer.setFullName(request.getFullName());
        customer.setPhone(request.getPhone());
        customer.setEmail(request.getEmail());
        return customerRepository.save(customer);
    }
}
