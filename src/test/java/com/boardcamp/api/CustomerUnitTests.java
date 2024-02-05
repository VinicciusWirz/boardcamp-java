package com.boardcamp.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.boardcamp.api.dtos.CustomerDTO;
import com.boardcamp.api.exceptions.CustomerCPFConflict;
import com.boardcamp.api.exceptions.CustomerNotFoundException;
import com.boardcamp.api.models.CustomerModel;
import com.boardcamp.api.repositories.CustomerRepository;
import com.boardcamp.api.services.CustomerService;

@SpringBootTest
@ActiveProfiles("test")
class CustomerUnitTests {
    @InjectMocks
    private CustomerService customerService;

    @Mock
    private CustomerRepository customerRepository;

    @Test
    void givenValidDTO_whenRegisteringNewCustomer_thenRegisterNewCustomer() {
        CustomerDTO dto = new CustomerDTO("name", "012345678901");
        CustomerModel newCustomer = new CustomerModel(dto);
        newCustomer.setId(1L);

        doReturn(false).when(customerRepository).existsByCpf(any());
        doReturn(newCustomer).when(customerRepository).save(any());

        CustomerModel result = customerService.save(dto);

        assertNotNull(result);
        assertEquals(newCustomer, result);
        verify(customerRepository, times(1)).existsByCpf(any());
        verify(customerRepository, times(1)).save(any());
    }

    @Test
    void givenRepeteadCPF_whenRegisteringNewCustomer_thenThrowsError() {
        CustomerDTO dto = new CustomerDTO("name", "012345678901");

        doReturn(true).when(customerRepository).existsByCpf(any());

        CustomerCPFConflict exception = assertThrows(CustomerCPFConflict.class, () -> customerService.save(dto));

        assertNotNull(exception);
        assertEquals("Customer is already registered", exception.getMessage());
        verify(customerRepository, times(1)).existsByCpf(any());
        verify(customerRepository, times(0)).save(any());
    }

    @Test
    void givenValidID_whenFindingCustomerById_thenReturnCustomer() {
        CustomerDTO dto = new CustomerDTO("name", "012345678901");
        CustomerModel newCustomer = new CustomerModel(dto);
        newCustomer.setId(1L);

        doReturn(Optional.of(newCustomer)).when(customerRepository).findById(any());

        CustomerModel result = customerService.findOne(any());

        assertNotNull(result);
        assertEquals(newCustomer, result);
        verify(customerRepository, times(1)).findById(any());
    }

    @Test
    void givenNonExistentID_whenFindingCustomerById_thenThrowsError() {
        doReturn(Optional.empty()).when(customerRepository).findById(any());

        CustomerNotFoundException exception = assertThrows(CustomerNotFoundException.class,
                () -> customerService.findOne(1L));

        assertNotNull(exception);
        assertEquals("Customer not found", exception.getMessage());
        verify(customerRepository, times(1)).findById(any());
    }

}
