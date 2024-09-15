package com.vladkostromin.bankpaymentproviderapp.repository;


import com.vladkostromin.bankpaymentproviderapp.entity.TransactionEntity;
import com.vladkostromin.bankpaymentproviderapp.enums.TransactionStatus;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

public interface TransactionRepository extends R2dbcRepository<TransactionEntity, UUID> {

    Mono<TransactionEntity> findTransactionEntityByTransactionId(UUID transactionId);

    Flux<TransactionEntity> findTransactionEntitiesByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    Flux<TransactionEntity> findTransactionEntitiesByTransactionStatus(TransactionStatus transactionStatus);


}
