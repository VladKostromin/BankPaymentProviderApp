package com.vladkostromin.bankpaymentproviderapp.service;

import com.vladkostromin.bankpaymentproviderapp.entity.CustomerEntity;
import com.vladkostromin.bankpaymentproviderapp.enums.Country;
import com.vladkostromin.bankpaymentproviderapp.exceptions.ObjectNotFoundException;
import com.vladkostromin.bankpaymentproviderapp.repository.CustomerRepository;
import com.vladkostromin.bankpaymentproviderapp.utils.CustomerDataUtils;
import com.vladkostromin.bankpaymentproviderapp.utils.UserDataUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
        //given
        CustomerEntity customerToSave = CustomerDataUtils.getCustomerTransient();
        BDDMockito.given(customerRepository.save(any(CustomerEntity.class)))
                .willReturn(Mono.just(CustomerDataUtils.getCustomerPersistent()));
        BDDMockito.given(userService.createUser())
                .willReturn(Mono.just(UserDataUtils.getUserData()));
        //when
        Mono<CustomerEntity> savedCustomer = customerServiceUnderTest.createCustomer(customerToSave);
        //then
        StepVerifier.create(savedCustomer)
                .assertNext(customer -> {
                    assertThat(customer).isNotNull();
                    assertThat(customer.getUser()).isNotNull();
                })
                .verifyComplete();
        verify(customerRepository, times(1)).save(any(CustomerEntity.class));
    }



    @Test
    @DisplayName("Test find customer by firstname lastname and country functionality")
    public void givenFirstNameLastNameCountry_whenFindCustomerByFirstNameLastNameAndCounty_thenCustomerIsReturned() {
        //give
        CustomerEntity customerToFind = CustomerDataUtils.getCustomerTransient();
        BDDMockito.given(customerRepository.findByFirstNameAndLastNameAndCountry(anyString(), anyString(), any(Country.class)))
                .willReturn(Mono.just(CustomerDataUtils.getCustomerPersistent()));
        BDDMockito.given(userService.getUserById(anyLong()))
                .willReturn(Mono.just(UserDataUtils.getUserData()));
        //when
        Mono<CustomerEntity> obtainedCustomer = customerServiceUnderTest.getCustomerByFirstNameLastNameAndCountry(customerToFind.getFirstName(), customerToFind.getLastName(), customerToFind.getCountry());
        //then
        StepVerifier.create(obtainedCustomer)
                .assertNext(customer -> {
                    assertThat(customer).isNotNull();
                    assertThat(customer.getUser()).isNotNull();
                })
                .verifyComplete();
        verify(customerRepository, times(1)).findByFirstNameAndLastNameAndCountry(anyString(), anyString(), any(Country.class));
        verify(userService, times(1)).getUserById(anyLong());
    }

    @Test
    @DisplayName("Test find customer by firstname lastname and country functionality")
    public void givenFirstNameLastNameCountry_whenFindCustomerByFirstNameLastNameAndCounty_thenExceptionIsThrown() {
        CustomerEntity customerToFind = CustomerDataUtils.getCustomerTransient();
        //give
        BDDMockito.given(customerRepository.findByFirstNameAndLastNameAndCountry(anyString(), anyString(), any(Country.class)))
                .willReturn(Mono.empty());
        //when
        Mono<CustomerEntity> obtainedCustomer = customerServiceUnderTest
                .getCustomerByFirstNameLastNameAndCountry(customerToFind.getFirstName(), customerToFind.getLastName(), customerToFind.getCountry());
        //then
        StepVerifier.create(obtainedCustomer)
                .expectErrorMatches(throwable -> throwable instanceof ObjectNotFoundException &&
                        throwable.getMessage().equals("Customer not found"))
                .verify();
        verify(customerRepository, times(1)).findByFirstNameAndLastNameAndCountry(anyString(), anyString(), any(Country.class));
        verify(userService, never()).getUserById(anyLong());
    }

    @Test
    @DisplayName("Test get customer by id functionality")
    public void givenCustomerId_whenGetCustomerById_thenCustomerIsReturned() {
        //given
        Long id = 1L;
        BDDMockito.given(customerRepository.findById(anyLong()))
                .willReturn(Mono.just(CustomerDataUtils.getCustomerPersistent()));
        //when
        Mono<CustomerEntity> obtainedCustomer = customerServiceUnderTest.getCustomerById(id);
        //then
        StepVerifier.create(obtainedCustomer)
                .assertNext(customer -> {
                    assertThat(customer).isNotNull();
                    assertThat(customer.getId()).isEqualTo(id);
                    assertThat(customer.getUser()).isNotNull();
                })
                .verifyComplete();
        verify(customerRepository, times(1)).findById(anyLong());
    }
    @Test
    @DisplayName("Test get customer by id functionality")
    public void givenCustomerId_whenGetCustomerById_thenExceptionIsThrown() {
        //given
        Long id = 1L;
        BDDMockito.given(customerRepository.findById(anyLong()))
                .willReturn(Mono.empty());
        //when
        Mono<CustomerEntity> obtainedCustomer = customerServiceUnderTest.getCustomerById(id);
        //then
        StepVerifier.create(obtainedCustomer)
                .expectErrorMatches(throwable -> throwable instanceof ObjectNotFoundException &&
                        throwable.getMessage().equals("Customer not found"))
                .verify();
        verify(customerRepository, times(1)).findById(anyLong());
    }
}
