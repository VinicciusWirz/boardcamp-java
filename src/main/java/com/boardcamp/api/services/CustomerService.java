package com.boardcamp.api.services;

import org.springframework.stereotype.Service;

import com.boardcamp.api.dtos.CustomerDTO;
import com.boardcamp.api.exceptions.CustomerCPFConflict;
import com.boardcamp.api.exceptions.CustomerNotFoundException;
import com.boardcamp.api.models.CustomerModel;
import com.boardcamp.api.repositories.CustomerRepository;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public CustomerModel findOne(Long id) {
        return customerRepository.findById(id).orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
    }

    public CustomerModel save(CustomerDTO dto) {
        if (customerRepository.existsByCpf(dto.getCpf())) {
            throw new CustomerCPFConflict("Customer is already registered");
        }

        CustomerModel customer = new CustomerModel(dto);
        return customerRepository.save(customer);
    }
}
