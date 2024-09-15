package com.vladkostromin.bankpaymentproviderapp.service;

import com.vladkostromin.bankpaymentproviderapp.entity.AccountEntity;
import com.vladkostromin.bankpaymentproviderapp.entity.CreditCardEntity;
import com.vladkostromin.bankpaymentproviderapp.entity.CustomerEntity;
import com.vladkostromin.bankpaymentproviderapp.entity.TransactionEntity;
import com.vladkostromin.bankpaymentproviderapp.enums.TransactionStatus;
import com.vladkostromin.bankpaymentproviderapp.enums.TransactionType;
import com.vladkostromin.bankpaymentproviderapp.exceptions.NotEnoughCurrencyException;
import com.vladkostromin.bankpaymentproviderapp.exceptions.ObjectNotFoundException;
import com.vladkostromin.bankpaymentproviderapp.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {
    private final TransactionalOperator transactionalOperator;
    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final CreditCardService creditCardService;
    private final CustomerService customerService;
    private final MerchantService merchantService;


    public Mono<TransactionEntity> createTransaction(TransactionEntity transactionEntity) {
        log.info("IN TransactionService.saveTransaction");
        return ReactiveSecurityContextHolder.getContext()
                .flatMap(securityContext -> merchantService.getMerchantByMerchantName(securityContext.getAuthentication().getName()))
                .flatMap(merchant -> accountService.getAccountByUserId(merchant.getUserId())
                        .flatMap(merchantAccount -> processCustomer(transactionEntity.getCustomer())
                                .flatMap(customer -> processCustomerAccount(customer)
                                        .flatMap(customerAccount -> processCreditCard(transactionEntity.getCardData(), customerAccount)
                                                .flatMap(creditCard -> setTransactionDetails(transactionEntity, customer, customerAccount, merchantAccount, creditCard))))))
                .as(transactionalOperator::transactional);
    }

    private Mono<CustomerEntity> processCustomer(CustomerEntity customer) {
        return customerService.getCustomerEntitiesByFirstNameAndLastNameAndCountry(
                        customer.getFirstName(),
                        customer.getLastName(),
                        customer.getCountry()
                )
                .onErrorResume(ObjectNotFoundException.class, e -> {
                    log.info("{}, creating new customer", e.getMessage());
                   return customerService.createCustomer(customer);
                });
    }

    private Mono<AccountEntity> processCustomerAccount(CustomerEntity customer) {
        return accountService.getAccountByUserId(customer.getUserId())
                .onErrorResume(ObjectNotFoundException.class, e -> {
                    log.info("{}, creating new account", e.getMessage());
                    return accountService.createAccount(customer.getUserId());
                });
    }

    private Mono<CreditCardEntity> processCreditCard(CreditCardEntity creditCardEntity, AccountEntity customerAccount) {
        return creditCardService.getCreditCardByCardNumber(creditCardEntity.getCardNumber())
                .onErrorResume(ObjectNotFoundException.class, e -> {
                    log.info("{}, creating new credit card", e.getMessage());
                    return creditCardService.createCreditCard(creditCardEntity, customerAccount);
                });
    }

    private Mono<TransactionEntity> setTransactionDetails(TransactionEntity transactionEntity, CustomerEntity customer, AccountEntity customerAccount, AccountEntity merchantAccount, CreditCardEntity creditCard) {
        if (transactionEntity.getTransactionType().equals(TransactionType.TOP_UP)) {
            if(customerAccount.getAmount() < transactionEntity.getAmount()) {
                return Mono.error(new NotEnoughCurrencyException("Insufficient funds for transaction"));
            }
            transactionEntity.setAccountFrom(customerAccount.getId());
            transactionEntity.setAccountTo(merchantAccount.getId());
        } else {
            if(merchantAccount.getAmount() < transactionEntity.getAmount()) {
                return Mono.error(new NotEnoughCurrencyException("Insufficient funds for transaction"));
            }
            transactionEntity.setAccountFrom(merchantAccount.getId());
            transactionEntity.setAccountTo(customerAccount.getId());
        }
        transactionEntity.setTransactionId(UUID.randomUUID());
        transactionEntity.setCustomerId(customer.getId());
        transactionEntity.setCardId(creditCard.getId());
        transactionEntity.setCreatedAt(LocalDateTime.now());
        transactionEntity.setUpdatedAt(LocalDateTime.now());
        transactionEntity.setTransactionStatus(TransactionStatus.IN_PROGRESS);
        return transactionRepository.save(transactionEntity);
    }

    public Mono<TransactionEntity> getTransactionByUUID(UUID id) {
        log.info("IN TransactionService.getTransactionById");
        return transactionRepository.findTransactionEntityByTransactionId(id)
                .switchIfEmpty(Mono.error(new ObjectNotFoundException("Transaction not found")))
                .flatMap(transaction -> {
                    Mono<CustomerEntity> MonoCustomer = customerService.getCustomerById(transaction.getCustomerId());
                    Mono<CreditCardEntity> MonoCreditCard = creditCardService.getCreditCardById(transaction.getCardId());

                    return Mono.zip(MonoCustomer, MonoCreditCard)
                            .flatMap(tuple -> {
                                CustomerEntity customerEntity = tuple.getT1();
                                CreditCardEntity creditCardEntity = tuple.getT2();
                                transaction.setCustomer(customerEntity);
                                transaction.setCardData(creditCardEntity);
                                return Mono.just(transaction);
                            });
                })
                .switchIfEmpty(Mono.error(new ObjectNotFoundException("Transaction not found")));
    }

    public Mono<TransactionEntity> updateTransaction(TransactionEntity transactionEntity) {
        log.info("IN TransactionService.updateTransaction");
        transactionEntity.setUpdatedAt(LocalDateTime.now());
        return transactionRepository.save(transactionEntity)
                .switchIfEmpty(Mono.error(new ObjectNotFoundException("Transaction not found")));
    }

    public Flux<TransactionEntity> getTransactionsForPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("IN TransactionService.getTransactionsForPeriod");
        return transactionRepository.findTransactionEntitiesByCreatedAtBetween(startDate, endDate)
                .switchIfEmpty(Mono.error(new ObjectNotFoundException("Transaction in period starts with: " + startDate + " and ends with: " + endDate + " not found")));
    }

    public Flux<TransactionEntity> getAllTransactionsByTransactionStatus(TransactionStatus transactionStatus) {
        log.info("IN TransactionService.getAllTransactionsInProgress");
        return transactionRepository.findTransactionEntitiesByTransactionStatus(transactionStatus)
                .switchIfEmpty(Mono.error(new ObjectNotFoundException("No transactions with: " + transactionStatus + " found")));
    }


}
