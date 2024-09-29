package com.vladkostromin.bankpaymentproviderapp.service;

import com.vladkostromin.bankpaymentproviderapp.entity.CustomerEntity;
import com.vladkostromin.bankpaymentproviderapp.enums.Country;
import com.vladkostromin.bankpaymentproviderapp.exceptions.ObjectNotFoundException;
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
                        .user(user)
                .build()));
    }
    public Mono<CustomerEntity> getCustomerByFirstNameLastNameAndCountry(String firstName, String lastName, Country country) {
        log.info("IN getCustomerEntitiesByFirstNameAndLastNameAndCountry");
        return customerRepository.findByFirstNameAndLastNameAndCountry(firstName, lastName, country)
                .switchIfEmpty(Mono.error(new ObjectNotFoundException("Customer not found")))
                .flatMap(customer -> userService.getUserById(customer.getUserId())
                        .flatMap(user -> {
                            customer.setUser(user);
                            return Mono.just(customer);
                        }));
    }

    public Mono<CustomerEntity> getCustomerById(Long customerId) {
        log.info("IN getCustomerById");
        return customerRepository.findById(customerId)
                .switchIfEmpty(Mono.error(new ObjectNotFoundException("Customer not found")));
    }
}
