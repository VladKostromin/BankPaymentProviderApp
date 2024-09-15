package com.vladkostromin.bankpaymentproviderapp.service;


import com.vladkostromin.bankpaymentproviderapp.entity.AccountEntity;
import com.vladkostromin.bankpaymentproviderapp.entity.CreditCardEntity;
import com.vladkostromin.bankpaymentproviderapp.exceptions.ObjectNotFoundException;
import com.vladkostromin.bankpaymentproviderapp.repository.AccountRepository;
import com.vladkostromin.bankpaymentproviderapp.utils.AccountDataUtils;
import com.vladkostromin.bankpaymentproviderapp.utils.CreditCardDataUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class AccountServiceTests {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CreditCardService creditCardService;

    @InjectMocks
    private AccountService accountServiceUnderTest;

    private AutoCloseable autoCloseable;

    @BeforeEach
    public void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);

        accountServiceUnderTest = new AccountService(accountRepository, creditCardService);
    }

    @AfterEach
    public void tearDown() throws Exception {
        autoCloseable.close();
    }


    @Test
    @DisplayName("Test save account functionality")
    public void givenAccountToCreate_whenCreateAccount_thenAccountIsCreated() {
        // given
        AccountEntity accountToCreate = AccountDataUtils.getAccountTransient();

        BDDMockito.given(accountRepository.save(any(AccountEntity.class)))
                .willReturn(Mono.just(AccountDataUtils.getAccountPersistent()));

        // when
        Mono<AccountEntity> createdAccount = accountServiceUnderTest.createAccount(accountToCreate.getUserId());

        // then
        StepVerifier.create(createdAccount)
                .assertNext(account -> {
                    assertThat(account).isNotNull();
                    assertThat(account.getId()).isNotEqualTo(accountToCreate.getId());
                })
                .verifyComplete();

        verify(accountRepository, times(1)).save(any(AccountEntity.class));
    }
    @Test
    @DisplayName("Test update account functionality")
    public void givenAccountToUpdate_whenAccountUpdated_thenAmountIsChanged() {
        //given
        AccountEntity currentAccount = AccountDataUtils.getAccountPersistent();
        AccountEntity accountToUpdate = currentAccount.toBuilder()
                .updatedAt(LocalDateTime.now().plusMinutes(1))
                .amount(currentAccount.getAmount() - 1000)
                .build();
        BDDMockito.given(accountRepository.findById(anyLong()))
                        .willReturn(Mono.just(currentAccount));
        BDDMockito.given(accountRepository.save(any(AccountEntity.class)))
                .willReturn(Mono.just(accountToUpdate));

        //when
        Mono<AccountEntity> updatedAccount = accountServiceUnderTest.updateAccount(accountToUpdate);

        //then
        StepVerifier.create(updatedAccount)
                .assertNext(account -> {
                    assertThat(account).isNotNull();
                    assertThat(account.getAmount()).isNotEqualByComparingTo(currentAccount.getAmount());
                    assertThat(account.getAmount()).isEqualTo(currentAccount.getAmount() - 1000);
                    assertThat(account.getUpdatedAt()).isAfter(currentAccount.getUpdatedAt());
                })
                .verifyComplete();
        verify(accountRepository, times(1)).save(any(AccountEntity.class));
    }

    @Test
    @DisplayName("Test update account functionality")
    public void givenAccountToUpdate_whenAccountUpdated_thenExceptionIsThrown() {
        // given
        AccountEntity accountToUpdate = AccountDataUtils.getAccountPersistent();
        BDDMockito.given(accountRepository.findById(accountToUpdate.getId()))
                .willReturn(Mono.empty());

        // when
        Mono<AccountEntity> updatedAccount = accountServiceUnderTest.updateAccount(accountToUpdate);

        // then
        StepVerifier.create(updatedAccount)
                .expectErrorMatches(throwable -> throwable instanceof ObjectNotFoundException &&
                        throwable.getMessage().equals("Account not found"))
                .verify();

        // verify
        verify(accountRepository, times(1)).findById(accountToUpdate.getId());
        verify(accountRepository, never()).save(any(AccountEntity.class));
    }

    @Test
    @DisplayName("Test get account by id functionality")
    public void givenAccountId_whenGetById_thenAccountIsReturned() {
        //given
        AccountEntity accountToFind = AccountDataUtils.getAccountPersistent();
        List<CreditCardEntity> creditCardDataList = CreditCardDataUtils.getCreditCardDataList();
        accountToFind.setCreditCards(creditCardDataList);

        BDDMockito.given(accountRepository.findById(accountToFind.getId()))
                .willReturn(Mono.just(accountToFind));
        BDDMockito.given(creditCardService.getAllCreditCardsByAccountId(anyLong()))
                .willReturn(Flux.fromIterable(creditCardDataList));

        // when
        Mono<AccountEntity> obtainedAccount = accountServiceUnderTest.getAccountById(accountToFind.getId());

        // then
        StepVerifier.create(obtainedAccount)
                .assertNext(account -> {
                    assertThat(account).isNotNull();
                    assertThat(account.getCreditCards()).hasSameSizeAs(creditCardDataList);
                })
                .verifyComplete();
        verify(accountRepository, times(1)).findById(accountToFind.getId());
        verify(creditCardService, times(1)).getAllCreditCardsByAccountId(anyLong());
    }

    @Test
    @DisplayName("Test get account by id functionality")
    public void givenAccountId_whenGetById_thenExceptionIsThrown() {
        //given
        BDDMockito.given(accountRepository.findById(anyLong()))
                .willReturn(Mono.empty());
        //when
        Mono<AccountEntity> obtainedAccount = accountServiceUnderTest.getAccountById(anyLong());
        //then
        StepVerifier.create(obtainedAccount)
                .expectErrorMatches(throwable -> throwable instanceof ObjectNotFoundException &&
                        throwable.getMessage().equals("Account not found"))
                .verify();
        verify(accountRepository, times(1)).findById(anyLong());

    }
    @Test
    @DisplayName("Test get account by userId functionality")
    public void givenUserId_whenGetAccountByUserId_thenAccountIsReturned() {
        //given
        AccountEntity accountToFind = AccountDataUtils.getAccountPersistent();
        List<CreditCardEntity> creditCardDataList = CreditCardDataUtils.getCreditCardDataList();
        accountToFind.setCreditCards(creditCardDataList);

        BDDMockito.given(accountRepository.findByUserId(anyLong()))
                .willReturn(Mono.just(accountToFind));
        BDDMockito.given(creditCardService.getAllCreditCardsByAccountId(anyLong()))
                .willReturn(Flux.fromIterable(creditCardDataList));

        // when
        Mono<AccountEntity> obtainedAccount = accountServiceUnderTest.getAccountByUserId(accountToFind.getUserId());

        // then
        StepVerifier.create(obtainedAccount)
                .assertNext(account -> {
                    assertThat(account).isNotNull();
                    assertThat(account.getCreditCards()).hasSameSizeAs(creditCardDataList);
                    assertThat(account.getCreditCards()).isEqualTo(creditCardDataList);
                })
                .verifyComplete();
        verify(accountRepository, times(1)).findByUserId(accountToFind.getUserId());
        verify(creditCardService, times(1)).getAllCreditCardsByAccountId(anyLong());
    }

    @Test
    @DisplayName("Test get account by userId functionality")
    public void givenUserId_whenGetAccountByUserId_thenExceptionIsThrown() {
        Long accountId = AccountDataUtils.getAccountPersistent().getId();
        BDDMockito.given(accountRepository.findByUserId(anyLong()))
                .willReturn(Mono.empty());
        //when
        Mono<AccountEntity> obtainedAccount = accountServiceUnderTest.getAccountByUserId(accountId);
        //then
        StepVerifier.create(obtainedAccount)
                .expectErrorMatches(throwable -> throwable instanceof ObjectNotFoundException &&
                        throwable.getMessage().equals("Account not found"))
                .verify();
        verify(accountRepository, times(1)).findByUserId(anyLong());
    }
}
