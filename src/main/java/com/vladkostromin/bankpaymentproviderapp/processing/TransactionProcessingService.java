package com.vladkostromin.bankpaymentproviderapp.processing;

import com.vladkostromin.bankpaymentproviderapp.entity.AccountEntity;
import com.vladkostromin.bankpaymentproviderapp.entity.TransactionEntity;
import com.vladkostromin.bankpaymentproviderapp.enums.TransactionStatus;
import com.vladkostromin.bankpaymentproviderapp.enums.TransactionType;
import com.vladkostromin.bankpaymentproviderapp.exceptions.ApiException;
import com.vladkostromin.bankpaymentproviderapp.exceptions.NotEnoughCurrencyException;
import com.vladkostromin.bankpaymentproviderapp.service.AccountService;
import com.vladkostromin.bankpaymentproviderapp.service.TransactionService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionProcessingService {

    private static final Duration DURATION_RETRY_INTERVAL = Duration.ofHours(1);

    private final TransactionService transactionService;
    private final AccountService accountService;
    private final WebClient webClient;



    @PostConstruct
    public void processTransactions() {


    }

    private Mono<TransactionEntity> finalizeTransaction(TransactionEntity transaction) {
        log.info("IN TransactionProcessingService.finalizeTransaction");
        if(transaction.getTransactionType().equals(TransactionType.TOP_UP)) {
            return processTopUpTransaction(transaction);
        } else if(transaction.getTransactionType().equals(TransactionType.PAY_OUT)) {
            return processPayOutTransaction(transaction);
        } else {
            return Mono.error(new ApiException("Invalid transaction type", "ERROR_CODE_TRANSACTION_TYPE_INVALID"));
        }
    }

    private Mono<TransactionEntity> processTopUpTransaction(TransactionEntity transaction) {
        log.info("IN processTopUpTransaction: {}", transaction);
        return accountService.getAccountById(transaction.getAccountFrom())
                .zipWith(accountService.getAccountById(transaction.getAccountTo()))
                .flatMap(tuple -> {
                    AccountEntity customerAccount = tuple.getT1();
                    AccountEntity merchantAccount = tuple.getT2();
                    if(customerAccount.getAmount() < transaction.getAmount()) {
                        transaction.setTransactionStatus(TransactionStatus.FAILED);
                        return transactionService.updateTransaction(transaction)
                                .then(Mono.error(new NotEnoughCurrencyException("Customer has not enough currency")));
                    }

                    customerAccount.setAmount(customerAccount.getAmount() - transaction.getAmount());
                    merchantAccount.setAmount(merchantAccount.getAmount() + transaction.getAmount());
                    transaction.setTransactionStatus(TransactionStatus.SUCCESS);
                    return accountService.updateAccount(customerAccount)
                            .then(accountService.updateAccount(merchantAccount))
                            .then(transactionService.updateTransaction(transaction));
                });
    }

    private Mono<TransactionEntity> processPayOutTransaction(TransactionEntity transaction) {
        log.info("IN processPayOutTransaction: {}", transaction);
        return accountService.getAccountById(transaction.getAccountFrom())
                .zipWith(accountService.getAccountById(transaction.getAccountTo()))
                .flatMap(tuple -> {
                    AccountEntity merchantAccount = tuple.getT1();
                    AccountEntity customerAccount = tuple.getT2();
                    if(merchantAccount.getAmount() < transaction.getAmount()) {
                        transaction.setTransactionStatus(TransactionStatus.FAILED);
                        transaction.setUpdatedAt(LocalDateTime.now());
                        return transactionService.updateTransaction(transaction)
                                .then(Mono.error(new NotEnoughCurrencyException("Merchant has not enough currency")));
                    }
                    merchantAccount.setAmount(merchantAccount.getAmount() - transaction.getAmount());
                    customerAccount.setAmount(customerAccount.getAmount() + transaction.getAmount());
                    transaction.setTransactionStatus(TransactionStatus.SUCCESS);
                    return accountService.updateAccount(customerAccount)
                            .then(accountService.updateAccount(merchantAccount))
                            .then(transactionService.updateTransaction(transaction));
                });
    }
}
