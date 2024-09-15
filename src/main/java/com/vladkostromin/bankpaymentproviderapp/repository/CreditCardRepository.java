package com.vladkostromin.bankpaymentproviderapp.repository;

import com.vladkostromin.bankpaymentproviderapp.entity.CreditCardEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CreditCardRepository extends R2dbcRepository<CreditCardEntity, Long> {

    Mono<CreditCardEntity> findByCardNumber(String cardNumber);
    Flux<CreditCardEntity> findAllByAccountId(Long accountId);
}
