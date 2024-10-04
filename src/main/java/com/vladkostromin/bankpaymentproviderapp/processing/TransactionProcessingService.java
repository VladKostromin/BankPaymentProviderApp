package com.vladkostromin.bankpaymentproviderapp.processing;

import com.vladkostromin.bankpaymentproviderapp.entity.AccountEntity;
import com.vladkostromin.bankpaymentproviderapp.entity.TransactionEntity;
import com.vladkostromin.bankpaymentproviderapp.enums.TransactionStatus;
import com.vladkostromin.bankpaymentproviderapp.service.AccountService;
import com.vladkostromin.bankpaymentproviderapp.service.TransactionService;
import com.vladkostromin.bankpaymentproviderapp.service.WebHookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
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
                .onErrorResume(e -> {
                    log.info(e.getMessage());
                    return Mono.empty();
                })
                .subscribe();

    }

    private Mono<TransactionEntity> finalizeTransaction(TransactionEntity transaction) {
        return webClient.post()
                .uri(BANK_API_URL)
                .bodyValue(transaction)
                .retrieve()
                .toEntity(TransactionEntity.class)
                .flatMap(responseEntity -> {
                    log.info("Successfully processed for transaction: {}", transaction.getTransactionId());
                    return handleBankResponse(responseEntity, transaction);
                })
                .onErrorResume(e -> {
                    HttpStatusCode status;
                    String errorMessage = e.getMessage();
                    if(e instanceof WebClientResponseException webClientResponseException) {
                        status = webClientResponseException.getStatusCode();
                        errorMessage = webClientResponseException.getResponseBodyAsString();
                    } else {
                        status = HttpStatus.INTERNAL_SERVER_ERROR;
                    }
                    log.error("Error processing transaction: {}", errorMessage);
                    ResponseEntity<TransactionEntity> errorResponse = ResponseEntity
                            .status(status)
                            .body(transaction);
                    return handleBankResponse(errorResponse, transaction);
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
            return revertFunds(transaction)
                    .then(transactionService.updateTransaction(transaction))
                    .then(webHookService.sendWebHook(transaction))
                    .thenReturn(transaction);
        }
    }
    private Mono<AccountEntity> revertFunds(TransactionEntity transaction) {
        return accountService.getAccountByUserId(transaction.getAccountFrom())
                .flatMap(accountFrom -> accountService.getAccountById(transaction.getAccountTo())
                        .flatMap(accountTo -> {
                            accountFrom.setAmount(accountFrom.getAmount() + transaction.getAmount());
                            accountTo.setAmount(accountTo.getAmount() - transaction.getAmount());
                            return accountService.updateAccount(accountFrom)
                                    .then(accountService.updateAccount(accountTo));
                        }));
    }

}
