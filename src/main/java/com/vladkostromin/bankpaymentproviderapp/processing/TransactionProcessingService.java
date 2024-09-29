package com.vladkostromin.bankpaymentproviderapp.processing;

import com.vladkostromin.bankpaymentproviderapp.entity.TransactionEntity;
import com.vladkostromin.bankpaymentproviderapp.enums.TransactionStatus;
import com.vladkostromin.bankpaymentproviderapp.service.AccountService;
import com.vladkostromin.bankpaymentproviderapp.service.TransactionService;
import com.vladkostromin.bankpaymentproviderapp.service.WebHookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionProcessingService {

    private static final String BANK_API_URL = "/api/bank-api/process-transaction";

    private final TransactionService transactionService;
    private final WebHookService webHookService;
    private final AccountService accountService;
    private final WebClient webClient;



    @Scheduled(fixedRate = 60000)
    public void processTransactions() {
        transactionService.getAllTransactionsByTransactionStatus(TransactionStatus.IN_PROGRESS)
                .flatMap(this::finalizeTransaction)
                .subscribe();

    }

    private Mono<TransactionEntity> finalizeTransaction(TransactionEntity transaction) {
        return webClient.post()
                .uri(BANK_API_URL)
                .bodyValue(transaction)
                .retrieve()
                .toEntity(TransactionEntity.class)
                .flatMap(responseEntity -> handleBankResponse(responseEntity, transaction))
                .onErrorResume(e -> {
                    log.error("Error processing transaction: {}", e.getMessage());
                    return Mono.empty();
                });
    }

    private Mono<TransactionEntity> handleBankResponse(ResponseEntity<TransactionEntity> responseEntity, TransactionEntity transaction) {
        if(responseEntity.getStatusCode().is2xxSuccessful()) {
            transaction.setTransactionStatus(TransactionStatus.SUCCESS);
            return transactionService.updateTransaction(transaction)
                    .then(webHookService.sendWebHook(transaction))
                    .thenReturn(transaction);
        } else {
            transaction.setTransactionStatus(TransactionStatus.FAILED);
            return transactionService.updateTransaction(transaction)
                    .then(webHookService.sendWebHook(transaction))
                    .thenReturn(transaction);
        }
    }

}
