package com.vladkostromin.bankpaymentproviderapp.repository;

import com.vladkostromin.bankpaymentproviderapp.entity.CustomerEntity;
import com.vladkostromin.bankpaymentproviderapp.enums.Country;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface CustomerRepository extends R2dbcRepository<CustomerEntity, Long> {
    Mono<CustomerEntity> findCustomerEntitiesByFirstNameAndLastNameAndCountry(String firstName, String lastName, Country country);

    Mono<CustomerEntity> getCustomerEntityByUserId(Long userIUd);
}
