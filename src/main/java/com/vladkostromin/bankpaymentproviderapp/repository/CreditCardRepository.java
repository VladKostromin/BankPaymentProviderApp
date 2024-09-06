package com.vladkostromin.bankpaymentproviderapp.repository;

import com.vladkostromin.bankpaymentproviderapp.entity.CreditCardEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface CreditCardRepository extends R2dbcRepository<CreditCardEntity, Long> {
    Mono<CreditCardEntity> findByAccountIdAndCardNumber(Long accountId, String cardNumber);
}
