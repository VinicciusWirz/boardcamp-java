package com.boardcamp.api.services;

import org.springframework.stereotype.Service;

import com.boardcamp.api.dtos.CustomerDTO;
import com.boardcamp.api.exceptions.CustomerCPFConflict;
import com.boardcamp.api.models.CustomerModel;
import com.boardcamp.api.repositories.CustomerRepository;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public String findOne(Long id) {
        return "To be implemented";
    }

    public CustomerModel save(CustomerDTO dto) {
        if (customerRepository.existsByCpf(dto.getCpf())) {
            throw new CustomerCPFConflict("User is already registered");
        }

        CustomerModel customer = new CustomerModel(dto);
        return customerRepository.save(customer);
    }
}
