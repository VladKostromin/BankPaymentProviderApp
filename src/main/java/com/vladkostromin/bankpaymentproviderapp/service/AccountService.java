package com.vladkostromin.bankpaymentproviderapp.service;

import com.vladkostromin.bankpaymentproviderapp.entity.AccountEntity;
import com.vladkostromin.bankpaymentproviderapp.exceptions.ObjectNotFoundException;
import com.vladkostromin.bankpaymentproviderapp.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {
    private final AccountRepository accountRepository;
    private final CreditCardService creditCardService;

    public Mono<AccountEntity> createAccount(Long userId) {
        log.info("IN Creating account with userId: {}", userId);
        return accountRepository.save(AccountEntity.builder()
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .amount(5000)
                        .userId(userId)
                .build());
    }

    public Mono<AccountEntity> updateAccount(AccountEntity account) {
        log.info("IN Updating account with userId: {}", account.getUserId());
        account.setUpdatedAt(LocalDateTime.now());
        return accountRepository.findById(account.getId())
                .switchIfEmpty(Mono.error(new ObjectNotFoundException("Account not found")))
                .flatMap(existingAccount -> accountRepository.save(account));
    }

    public Mono<AccountEntity> getAccountById(Long accountId) {
        log.info("IN Getting account with accountId: {}", accountId);

        return accountRepository.findById(accountId)
                .switchIfEmpty(Mono.error(new ObjectNotFoundException("Account not found")))
                .flatMap(account -> creditCardService.getAllCreditCardsByAccountId(account.getId())
                        .collectList()
                        .map(creditCards -> {
                            account.setCreditCards(creditCards);
                            return account;
                        }));
    }

    public Mono<AccountEntity> getAccountByUserId(Long userId) {
        log.info("IN Getting accountByUserId with userId: {}", userId);
        return accountRepository.findByUserId(userId)
                .switchIfEmpty(Mono.error(new ObjectNotFoundException("Account not found")))
                .flatMap(account -> creditCardService.getAllCreditCardsByAccountId(account.getId())
                        .collectList()
                        .onErrorResume(ObjectNotFoundException.class, e -> {
                            log.info("No credit cards found for accountId: {}. Returning account with empty credit card list.", account.getId());
                            return Mono.just(Collections.emptyList());
                        })
                        .map(creditCards -> {
                            account.setCreditCards(creditCards);
                            return account;
                        }));
    }

}
