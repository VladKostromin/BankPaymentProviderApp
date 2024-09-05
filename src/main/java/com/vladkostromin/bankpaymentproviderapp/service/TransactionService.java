package com.vladkostromin.bankpaymentproviderapp.service;

import com.vladkostromin.bankpaymentproviderapp.entity.*;
import com.vladkostromin.bankpaymentproviderapp.enums.TransactionStatus;
import com.vladkostromin.bankpaymentproviderapp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;
    private final CreditCardRepository creditCardRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    public Mono<TransactionEntity> saveTransaction(TransactionEntity transactionEntity) {
        return customerRepository.findCustomerEntitiesByFirstNameAndLastNameAndCountry(
                transactionEntity.getCustomer().getFirstName(),
                transactionEntity.getCustomer().getLastName(),
                transactionEntity.getCustomer().getCountry()
        )
                .flatMap(customer -> {
                    transactionEntity.setCustomerId(customer.getId());
                    return accountRepository.findAccountEntitiesByUserId(customer.getUserId())
                            .flatMap(account -> addOrFindCreditCard(transactionEntity.getCardData(), account)
                                    .flatMap(creditCard -> {
                                        transactionEntity.setCardId(creditCard.getId());
                                        transactionEntity.setAccountFrom(account.getId());
                                        transactionEntity.setAccountTo(1L);
                                        transactionEntity.setCreatedAt(LocalDateTime.now());
                                        transactionEntity.setUpdatedAt(LocalDateTime.now());
                                        transactionEntity.setTransactionStatus(TransactionStatus.IN_PROGRESS);
                                        return transactionRepository.save(transactionEntity);
                                    }))
                            .flatMap(account -> transactionRepository.save(transactionEntity));
                })
                .switchIfEmpty(createNewCustomerAndProcessTransaction(transactionEntity));
    }

    private Mono<CreditCardEntity> addOrFindCreditCard(CreditCardEntity creditCard, AccountEntity account) {
        return creditCardRepository.findByAccountIdAndCardNumber(account.getId(), creditCard.getCardNumber())
                .switchIfEmpty(Mono.defer(() -> {
                    creditCard.setAccountId(account.getId());
                    creditCard.setCreatedAt(LocalDateTime.now());
                    creditCard.setUpdatedAt(LocalDateTime.now());
                    return creditCardRepository.save(creditCard);
                }));
    }

    private Mono<TransactionEntity> createNewCustomerAndProcessTransaction(TransactionEntity transactionEntity) {
        return userRepository.save(UserEntity.builder()
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                .build())
                .flatMap(user -> {
                    CustomerEntity newCustomer = CustomerEntity.builder()
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .firstName(transactionEntity.getCustomer().getFirstName())
                            .lastName(transactionEntity.getCustomer().getLastName())
                            .country(transactionEntity.getCustomer().getCountry())
                            .userId(user.getId())
                            .build();
                    return customerRepository.save(newCustomer)
                            .flatMap(customer -> {
                                transactionEntity.setCustomerId(customer.getId());
                                AccountEntity newAccount = AccountEntity.builder()
                                        .createdAt(LocalDateTime.now())
                                        .updatedAt(LocalDateTime.now())
                                        .userId(user.getId())
                                        .amount(1000)
                                        .build();
                                return accountRepository.save(newAccount)
                                        .flatMap(account -> addOrFindCreditCard(transactionEntity.getCardData(), newAccount)
                                                .flatMap(creditCard -> {
                                                    transactionEntity.setCardId(creditCard.getId());
                                                    transactionEntity.setCustomerId(customer.getId());
                                                    transactionEntity.setAccountFrom(account.getId());
                                                    transactionEntity.setAccountTo(1L);
                                                    transactionEntity.setCreatedAt(LocalDateTime.now());
                                                    transactionEntity.setUpdatedAt(LocalDateTime.now());
                                                    transactionEntity.setTransactionStatus(TransactionStatus.IN_PROGRESS);
                                                    return transactionRepository.save(transactionEntity);
                                                }));
                            });
                })
                .flatMap(account -> transactionRepository.save(transactionEntity));
    }

}
