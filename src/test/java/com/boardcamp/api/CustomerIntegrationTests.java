package com.boardcamp.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.boardcamp.api.dtos.CustomerDTO;
import com.boardcamp.api.models.CustomerModel;
import com.boardcamp.api.repositories.CustomerRepository;

import jakarta.validation.ValidationException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CustomerIntegrationTests {
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    @AfterEach
    void cleanUp() {
        customerRepository.deleteAll();
    }

    @Test
    void givenValidDTO_whenRegisteringNewCustomer_thenRegisterNewCustomer() {
        CustomerDTO dto = new CustomerDTO("name", "01234567890");

        HttpEntity<CustomerDTO> body = new HttpEntity<>(dto);

        ResponseEntity<CustomerModel> response = testRestTemplate.exchange(
                "/customers",
                HttpMethod.POST,
                body,
                CustomerModel.class);

        CustomerModel expectedBody = new CustomerModel(dto);
        expectedBody.setId(response.getBody().getId());

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedBody, response.getBody());
        assertEquals(1, customerRepository.count());
    }

    @Test
    void givenInvalidDTO_whenRegisteringNewCustomer_thenThrowsError() {
        CustomerDTO dto = new CustomerDTO("", "012");

        HttpEntity<CustomerDTO> body = new HttpEntity<>(dto);

        ResponseEntity<ValidationException> response = testRestTemplate.exchange(
                "/customers",
                HttpMethod.POST,
                body,
                ValidationException.class);

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Validation failed for object='customerDTO'. Error count: 2", response.getBody().getMessage());
        assertEquals(0, customerRepository.count());
    }

    @Test
    void givenAlreadyRegisteredCPF_whenRegisteringNewCustomer_thenThrowsError() {
        CustomerDTO dto = new CustomerDTO("name", "01234567890");

        CustomerModel oldCustomer = new CustomerModel(dto);
        customerRepository.save(oldCustomer);

        HttpEntity<CustomerDTO> body = new HttpEntity<>(dto);

        ResponseEntity<String> response = testRestTemplate.exchange(
                "/customers",
                HttpMethod.POST,
                body,
                String.class);

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Customer is already registered", response.getBody());
        assertEquals(1, customerRepository.count());
    }

    @Test
    void givenValidID_whenFindingCustomerById_thenReturnCustomer() {
        CustomerDTO dto = new CustomerDTO("name", "01234567890");

        CustomerModel customer = new CustomerModel(dto);
        CustomerModel savedCustomer = customerRepository.save(customer);

        ResponseEntity<CustomerModel> response = testRestTemplate.exchange(
                "/customers/{id}",
                HttpMethod.GET,
                null,
                CustomerModel.class,
                savedCustomer.getId());

        assertNotNull(response.getBody());
        assertEquals(savedCustomer, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, customerRepository.count());
    }

    @Test
    void givenInvalidID_whenFindingCustomerById_thenThrowsError() {

        ResponseEntity<String> response = testRestTemplate.exchange(
                "/customers/{id}",
                HttpMethod.GET,
                null,
                String.class,
                1L);

        assertNotNull(response.getBody());
        assertEquals("Customer not found", response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(0, customerRepository.count());
    }
}
