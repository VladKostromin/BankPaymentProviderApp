package com.vladkostromin.bankpaymentproviderapp.service;

import com.vladkostromin.bankpaymentproviderapp.entity.AccountEntity;
import com.vladkostromin.bankpaymentproviderapp.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

    public Mono<AccountEntity> createAccount(Long userId) {
        return accountRepository.save(AccountEntity.builder()
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .amount(1000)
                        .userId(userId)
                .build());
    }

    public Mono<AccountEntity> updateAccount(AccountEntity account) {
        account.setUpdatedAt(LocalDateTime.now());
        return accountRepository.save(account);
    }

    public Mono<AccountEntity> getAccountById(Long accountId) {
        return accountRepository.findById(accountId);
    }

    public Mono<AccountEntity> getAccountByUserId(Long userId) {
        return accountRepository.findAccountEntitiesByUserId(userId);
    }

}
