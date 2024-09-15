package com.vladkostromin.bankpaymentproviderapp.service;

import com.vladkostromin.bankpaymentproviderapp.repository.CreditCardRepository;
import com.vladkostromin.bankpaymentproviderapp.repository.CustomerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CustomerServiceTests {


    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private CustomerService customerServiceUnderTest;

    private AutoCloseable autoCloseable;

    @BeforeEach
    public void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);

        customerServiceUnderTest = new CustomerService(customerRepository, userService);
    }

    @AfterEach
    public void tearDown() throws Exception {
        autoCloseable.close();
    }



    @Test
    @DisplayName("Test create customer functionality")
    public void givenCustomerEntity_whenCreateCustomer_thenCustomerIsCreated() {

    }

    @Test
    public void givenFirstNameLastNameCountry_whenFindCustomerByFirstNameLastNameAndCounty_thenCustomerIsReturned() {

    }

    @Test
    public void givenCustomerId_whenFindCustomerById_thenCustomerIsReturned() {

    }
}
