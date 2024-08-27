package com.vladkostromin.bankpaymentproviderapp.service;

import com.vladkostromin.bankpaymentproviderapp.entity.TransactionEntity;
import com.vladkostromin.bankpaymentproviderapp.enums.TransactionStatus;
import com.vladkostromin.bankpaymentproviderapp.enums.TransactionType;
import com.vladkostromin.bankpaymentproviderapp.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final TransactionRepository transactionRepository;

    public Mono<TransactionEntity> saveTransaction(TransactionEntity transactionEntity) {
        return transactionRepository.save(transactionEntity.toBuilder()
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .transactionType(TransactionType.WITHDRAWAL)
                .transactionStatus(TransactionStatus.IN_PROGRESS)
                .build());
    }


}
