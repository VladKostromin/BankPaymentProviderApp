package com.vladkostromin.bankpaymentproviderapp.service;

import com.vladkostromin.bankpaymentproviderapp.entity.*;
import com.vladkostromin.bankpaymentproviderapp.enums.TransactionStatus;
import com.vladkostromin.bankpaymentproviderapp.enums.TransactionType;
import com.vladkostromin.bankpaymentproviderapp.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final CreditCardService creditCardService;
    private final CustomerService customerService;
    private final MerchantService merchantService;

    @Transactional
    public Mono<TransactionEntity> createTransaction(TransactionEntity transactionEntity) {
        log.info("IN TransactionService.saveTransaction");

        return ReactiveSecurityContextHolder.getContext()
                .flatMap(securityContext -> {
                    String merchantName = securityContext.getAuthentication().getName();
                    return merchantService.getMerchantByMerchantName(merchantName)
                            .flatMap(merchant -> processMerchantAccount(transactionEntity, merchant));
                });
    }

    private Mono<TransactionEntity> processMerchantAccount(TransactionEntity transactionEntity, MerchantEntity merchant) {
        return accountService.getAccountByUserId(merchant.getUserId())
                .flatMap(merchantAccount -> processCustomer(transactionEntity, merchantAccount));
    }

    private Mono<TransactionEntity> processCustomer(TransactionEntity transactionEntity, AccountEntity merchantAccount) {
        return customerService.getCustomerEntitiesByFirstNameAndLastNameAndCountry(
                        transactionEntity.getCustomer().getFirstName(),
                        transactionEntity.getCustomer().getLastName(),
                        transactionEntity.getCustomer().getCountry()
                )
                .switchIfEmpty(customerService.createCustomer(transactionEntity.getCustomer()))
                .flatMap(customer -> processCustomerAccount(transactionEntity, customer, merchantAccount));
    }

    private Mono<TransactionEntity> processCustomerAccount(TransactionEntity transactionEntity, CustomerEntity customer, AccountEntity merchantAccount) {
        return accountService.getAccountByUserId(customer.getUserId())
                .flatMap(customerAccount -> processCreditCard(transactionEntity, customer, customerAccount, merchantAccount));
    }

    private Mono<TransactionEntity> processCreditCard(TransactionEntity transactionEntity, CustomerEntity customer, AccountEntity customerAccount, AccountEntity merchantAccount) {
        return creditCardService.addOrFindCreditCard(transactionEntity.getCardData(), customerAccount)
                .flatMap(creditCard -> setTransactionDetails(transactionEntity, customer, customerAccount, merchantAccount, creditCard));
    }

    private Mono<TransactionEntity> setTransactionDetails(TransactionEntity transactionEntity, CustomerEntity customer, AccountEntity customerAccount, AccountEntity merchantAccount, CreditCardEntity creditCard) {
        transactionEntity.setCustomerId(customer.getId());
        transactionEntity.setCardId(creditCard.getId());

        if (transactionEntity.getTransactionType().equals(TransactionType.TOP_UP)) {
            transactionEntity.setAccountFrom(customerAccount.getId());
            transactionEntity.setAccountTo(merchantAccount.getId());
        } else {
            transactionEntity.setAccountFrom(merchantAccount.getId());
            transactionEntity.setAccountTo(customerAccount.getId());
        }

        transactionEntity.setCreatedAt(LocalDateTime.now());
        transactionEntity.setUpdatedAt(LocalDateTime.now());
        transactionEntity.setTransactionStatus(TransactionStatus.IN_PROGRESS);

        log.info("Transaction before saving: {}", transactionEntity);
        return transactionRepository.save(transactionEntity);
    }

    public Mono<TransactionEntity> getTransactionById(UUID id) {
        log.info("IN TransactionService.getTransactionById");
        return transactionRepository.findById(id);
    }

    public Mono<TransactionEntity> updateTransaction(TransactionEntity transactionEntity) {
        log.info("IN TransactionService.updateTransaction");
        transactionEntity.setUpdatedAt(LocalDateTime.now());
        return transactionRepository.save(transactionEntity);
    }

    public Flux<TransactionEntity> getTransactionsForPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("IN TransactionService.getTransactionsForPeriod");
        return transactionRepository.findTransactionEntitiesByCreatedAtBetween(startDate, endDate);
    }

    public Flux<TransactionEntity> getAllTransactionsByTransactionStatus(TransactionStatus transactionStatus) {
        log.info("IN TransactionService.getAllTransactionsInProgress");
        return transactionRepository.findTransactionEntitiesByTransactionStatus(transactionStatus);
    }


}
