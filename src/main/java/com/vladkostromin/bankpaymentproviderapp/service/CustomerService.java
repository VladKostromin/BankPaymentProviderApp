package com.vladkostromin.bankpaymentproviderapp.service;

import com.vladkostromin.bankpaymentproviderapp.entity.CustomerEntity;
import com.vladkostromin.bankpaymentproviderapp.enums.Country;
import com.vladkostromin.bankpaymentproviderapp.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final UserService userService;

    public Mono<CustomerEntity> createCustomer(CustomerEntity customerEntity) {
        log.info("IN createCustomer");
        return userService.createUser()
                .flatMap(user -> customerRepository.save(customerEntity.toBuilder()
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .firstName(customerEntity.getFirstName())
                        .lastName(customerEntity.getLastName())
                        .country(customerEntity.getCountry())
                        .userId(user.getId())
                .build()));
    }
    public Mono<CustomerEntity> getCustomerEntitiesByFirstNameAndLastNameAndCountry(String firstName, String lastName, Country country) {
        log.info("IN getCustomerEntitiesByFirstNameAndLastNameAndCountry");
        return customerRepository.findCustomerEntitiesByFirstNameAndLastNameAndCountry(firstName, lastName, country);
    }

    public Mono<CustomerEntity> getCustomerEntityByUserId(Long userId) {
        log.info("IN getCustomerEntityByUserId");
        return customerRepository.getCustomerEntityByUserId(userId);
    }

    public Mono<CustomerEntity> updateCustomer(CustomerEntity customerEntity) {
        log.info("IN updateCustomer");
        customerEntity.setUpdatedAt(LocalDateTime.now());
        return customerRepository.save(customerEntity);
    }

    public Mono<CustomerEntity> getCustomerById(Long customerId) {
        log.info("IN getCustomerById");
        return customerRepository.findById(customerId);
    }

    public Mono<CustomerEntity> deleteCustomer(Long customerId) {
        log.info("IN deleteCustomer");
        return customerRepository.findById(customerId);
    }
}
