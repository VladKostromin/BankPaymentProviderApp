package com.vladkostromin.bankpaymentproviderapp.service;

import com.vladkostromin.bankpaymentproviderapp.entity.*;
import com.vladkostromin.bankpaymentproviderapp.enums.Country;
import com.vladkostromin.bankpaymentproviderapp.enums.TransactionStatus;
import com.vladkostromin.bankpaymentproviderapp.exceptions.NotEnoughCurrencyException;
import com.vladkostromin.bankpaymentproviderapp.exceptions.ObjectNotFoundException;
import com.vladkostromin.bankpaymentproviderapp.repository.TransactionRepository;
import com.vladkostromin.bankpaymentproviderapp.utils.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTests {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private AccountService accountService;
    @Mock
    private CreditCardService creditCardService;
    @Mock
    private CustomerService customerService;
    @Mock
    private MerchantService merchantService;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;
    @Mock
    private TransactionalOperator transactionalOperator;

    @InjectMocks
    private TransactionService transactionServiceUnderTest;

    private AutoCloseable autoCloseable;

    @BeforeEach
    public void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);

        transactionServiceUnderTest = new TransactionService(transactionalOperator, transactionRepository, accountService, creditCardService, customerService, merchantService);
    }

    @AfterEach
    public void tearDown() throws Exception {
        autoCloseable.close();
    }




    @Test
    @DisplayName("Test create transaction functionality")
    public void givenTransactionEntity_whenCreateTransaction_thenTransactionIsCreated() {
        //given
        try (MockedStatic<ReactiveSecurityContextHolder> mockedContextHolder = Mockito.mockStatic(ReactiveSecurityContextHolder.class)) {
            BDDMockito.given(authentication.getName()).willReturn(MerchantDataUtils.getMerchantDataPersisted().getMerchantName());
            BDDMockito.given(securityContext.getAuthentication()).willReturn(authentication);
            mockedContextHolder.when(ReactiveSecurityContextHolder::getContext).thenReturn(Mono.just(securityContext));

            BDDMockito.given(transactionalOperator.transactional(any(Mono.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));

            BDDMockito.given(customerService.getCustomerByFirstNameLastNameAndCountry(anyString(), anyString(), any(Country.class)))
                    .willReturn(Mono.just(CustomerDataUtils.getCustomerPersistent()));
            BDDMockito.given(accountService.getAccountByUserId(anyLong()))
                    .willReturn(Mono.just(AccountDataUtils.getAccountPersistent()));
            BDDMockito.given(creditCardService.getCreditCardByCardNumber(anyString()))
                    .willReturn(Mono.just(CreditCardDataUtils.getCreditCardDataPersistent()));
            BDDMockito.given(merchantService.getMerchantByMerchantName(anyString()))
                    .willReturn(Mono.just(MerchantDataUtils.getMerchantDataPersisted()));
            BDDMockito.given(transactionRepository.save(any(TransactionEntity.class)))
                    .willReturn(Mono.just(TransactionDataUtils.getTransactionPersistent()));
            BDDMockito.given(accountService.updateAccount(any(AccountEntity.class)))
                    .willReturn(Mono.just(AccountDataUtils.getAccountPersistent()));

            //when
            Mono<TransactionEntity> savedTransaction = transactionServiceUnderTest.createTransaction(TransactionDataUtils.getTransactionPersistent());

            //then
            StepVerifier.create(savedTransaction)
                    .assertNext(transaction -> {
                        assertThat(transaction).isNotNull();
                        assertThat(transaction.getCustomer()).isNotNull();
                        assertThat(transaction.getCardData()).isNotNull();
                    })
                    .verifyComplete();

            verify(merchantService, times(1)).getMerchantByMerchantName(anyString());
            verify(accountService, times(2)).getAccountByUserId(anyLong());
            verify(customerService, times(1)).getCustomerByFirstNameLastNameAndCountry(anyString(), anyString(), any(Country.class));
            verify(creditCardService, times(1)).getCreditCardByCardNumber(anyString());
            verify(transactionRepository, times(1)).save(any(TransactionEntity.class));
        }
    }
    @Test
    @DisplayName("Testing create transaction functionality")
    public void given_TransactionEntity_whenCreateTransaction_thenNewCustomerAndTransactionIsCreated() {
        //given
        try (MockedStatic<ReactiveSecurityContextHolder> mockedContextHolder = Mockito.mockStatic(ReactiveSecurityContextHolder.class)) {
            BDDMockito.given(authentication.getName()).willReturn(MerchantDataUtils.getMerchantDataPersisted().getMerchantName());
            BDDMockito.given(securityContext.getAuthentication()).willReturn(authentication);
            mockedContextHolder.when(ReactiveSecurityContextHolder::getContext).thenReturn(Mono.just(securityContext));

            BDDMockito.given(transactionalOperator.transactional(any(Mono.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));

            BDDMockito.given(customerService.getCustomerByFirstNameLastNameAndCountry(anyString(), anyString(), any(Country.class)))
                    .willReturn(Mono.error(new ObjectNotFoundException("Customer not found")));
            BDDMockito.given(customerService.createCustomer(any(CustomerEntity.class)))
                    .willReturn(Mono.just(CustomerDataUtils.getCustomerPersistent()));
            BDDMockito.given(accountService.getAccountByUserId(anyLong()))
                    .willReturn(Mono.just(AccountDataUtils.getAccountPersistent()));
            BDDMockito.given(creditCardService.getCreditCardByCardNumber(anyString()))
                    .willReturn(Mono.just(CreditCardDataUtils.getCreditCardDataPersistent()));
            BDDMockito.given(merchantService.getMerchantByMerchantName(anyString()))
                    .willReturn(Mono.just(MerchantDataUtils.getMerchantDataPersisted()));
            BDDMockito.given(transactionRepository.save(any(TransactionEntity.class)))
                    .willReturn(Mono.just(TransactionDataUtils.getTransactionPersistent()));
            BDDMockito.given(accountService.updateAccount(any(AccountEntity.class)))
                    .willReturn(Mono.just(AccountDataUtils.getAccountPersistent()));


            //when
            Mono<TransactionEntity> savedTransaction = transactionServiceUnderTest.createTransaction(TransactionDataUtils.getTransactionPersistent());

            //then
            StepVerifier.create(savedTransaction)
                    .assertNext(transaction -> {
                        assertThat(transaction).isNotNull();
                        assertThat(transaction.getCustomer()).isNotNull();
                        assertThat(transaction.getCardData()).isNotNull();
                    })
                    .verifyComplete();

            verify(merchantService, times(1)).getMerchantByMerchantName(anyString());
            verify(accountService, times(2)).getAccountByUserId(anyLong());
            verify(customerService, times(1)).createCustomer(any(CustomerEntity.class));
            verify(customerService, times(1)).getCustomerByFirstNameLastNameAndCountry(anyString(), anyString(), any(Country.class));
            verify(creditCardService, times(1)).getCreditCardByCardNumber(anyString());
            verify(transactionRepository, times(1)).save(any(TransactionEntity.class));
        }

    }
    @Test
    @DisplayName("Testing create transaction functionality")
    public void given_TransactionEntity_whenCreateTransaction_thenNewAccountAndTransactionIsCreated() {
        //given
        try (MockedStatic<ReactiveSecurityContextHolder> mockedContextHolder = Mockito.mockStatic(ReactiveSecurityContextHolder.class)) {
            CustomerEntity customer = CustomerDataUtils.getCustomerPersistent();
            MerchantEntity merchant = MerchantDataUtils.getMerchantDataPersisted();
            AccountEntity merchantAccount = AccountDataUtils.getAccountPersistent();
            merchant.getUser().setId(2L);
            merchant.setUserId(2L);
            merchantAccount.setId(2L);
            merchantAccount.setUserId(2L);

            BDDMockito.given(authentication.getName()).willReturn(MerchantDataUtils.getMerchantDataPersisted().getMerchantName());
            BDDMockito.given(securityContext.getAuthentication()).willReturn(authentication);
            mockedContextHolder.when(ReactiveSecurityContextHolder::getContext).thenReturn(Mono.just(securityContext));

            BDDMockito.given(transactionalOperator.transactional(any(Mono.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));

            BDDMockito.given(customerService.getCustomerByFirstNameLastNameAndCountry(anyString(), anyString(), any(Country.class)))
                    .willReturn(Mono.just(CustomerDataUtils.getCustomerPersistent()));

            BDDMockito.given(accountService.getAccountByUserId(merchant.getUserId()))
                    .willReturn(Mono.just(merchantAccount));

            BDDMockito.given(accountService.getAccountByUserId(customer.getUserId()))
                            .willReturn(Mono.error(new ObjectNotFoundException("Account not found")));

            BDDMockito.given(accountService.createAccount(anyLong()))
                            .willReturn(Mono.just(AccountDataUtils.getAccountPersistent()));

            BDDMockito.given(creditCardService.getCreditCardByCardNumber(anyString()))
                    .willReturn(Mono.just(CreditCardDataUtils.getCreditCardDataPersistent()));

            BDDMockito.given(merchantService.getMerchantByMerchantName(anyString()))
                    .willReturn(Mono.just(merchant));

            BDDMockito.given(transactionRepository.save(any(TransactionEntity.class)))
                    .willReturn(Mono.just(TransactionDataUtils.getTransactionPersistent()));

            BDDMockito.given(accountService.updateAccount(any(AccountEntity.class)))
                    .willReturn(Mono.just(AccountDataUtils.getAccountPersistent()));

            //when
            Mono<TransactionEntity> savedTransaction = transactionServiceUnderTest.createTransaction(TransactionDataUtils.getTransactionPersistent());

            //then
            StepVerifier.create(savedTransaction)
                    .assertNext(transaction -> {
                        assertThat(transaction).isNotNull();
                        assertThat(transaction.getCustomer()).isNotNull();
                        assertThat(transaction.getCardData()).isNotNull();
                    })
                    .verifyComplete();

            verify(merchantService, times(1)).getMerchantByMerchantName(anyString());
            verify(accountService, times(2)).getAccountByUserId(anyLong());
            verify(accountService, times(1)).createAccount(anyLong());
            verify(customerService, times(1)).getCustomerByFirstNameLastNameAndCountry(anyString(), anyString(), any(Country.class));
            verify(creditCardService, times(1)).getCreditCardByCardNumber(anyString());
            verify(transactionRepository, times(1)).save(any(TransactionEntity.class));
        }
    }
    @Test
    @DisplayName("Testing create transaction functionality")
    public void given_TransactionEntity_whenCreateTransaction_thenCreditCardAndTransactionIsCreated() {
        //given
        try (MockedStatic<ReactiveSecurityContextHolder> mockedContextHolder = Mockito.mockStatic(ReactiveSecurityContextHolder.class)) {
            BDDMockito.given(authentication.getName()).willReturn(MerchantDataUtils.getMerchantDataPersisted().getMerchantName());
            BDDMockito.given(securityContext.getAuthentication()).willReturn(authentication);
            mockedContextHolder.when(ReactiveSecurityContextHolder::getContext).thenReturn(Mono.just(securityContext));

            BDDMockito.given(transactionalOperator.transactional(any(Mono.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));

            BDDMockito.given(customerService.getCustomerByFirstNameLastNameAndCountry(anyString(), anyString(), any(Country.class)))
                    .willReturn(Mono.just(CustomerDataUtils.getCustomerPersistent()));
            BDDMockito.given(accountService.getAccountByUserId(anyLong()))
                    .willReturn(Mono.just(AccountDataUtils.getAccountPersistent()));
            BDDMockito.given(creditCardService.getCreditCardByCardNumber(anyString()))
                    .willReturn(Mono.error(new ObjectNotFoundException("Credit Card not found")));
            BDDMockito.given(creditCardService.createCreditCard(any(CreditCardEntity.class), any(AccountEntity.class)))
                            .willReturn(Mono.just(CreditCardDataUtils.getCreditCardDataPersistent()));
            BDDMockito.given(merchantService.getMerchantByMerchantName(anyString()))
                    .willReturn(Mono.just(MerchantDataUtils.getMerchantDataPersisted()));
            BDDMockito.given(transactionRepository.save(any(TransactionEntity.class)))
                    .willReturn(Mono.just(TransactionDataUtils.getTransactionPersistent()));
            BDDMockito.given(accountService.updateAccount(any(AccountEntity.class)))
                    .willReturn(Mono.just(AccountDataUtils.getAccountPersistent()));

            //when
            Mono<TransactionEntity> savedTransaction = transactionServiceUnderTest.createTransaction(TransactionDataUtils.getTransactionPersistent());

            //then
            StepVerifier.create(savedTransaction)
                    .assertNext(transaction -> {
                        assertThat(transaction).isNotNull();
                        assertThat(transaction.getCustomer()).isNotNull();
                        assertThat(transaction.getCardData()).isNotNull();
                    })
                    .verifyComplete();

            verify(merchantService, times(1)).getMerchantByMerchantName(anyString());
            verify(accountService, times(2)).getAccountByUserId(anyLong());
            verify(customerService, times(1)).getCustomerByFirstNameLastNameAndCountry(anyString(), anyString(), any(Country.class));
            verify(creditCardService, times(1)).getCreditCardByCardNumber(anyString());
            verify(creditCardService, times(1)).createCreditCard(any(CreditCardEntity.class), any(AccountEntity.class));
            verify(transactionRepository, times(1)).save(any(TransactionEntity.class));
        }

    }
    @Test
    @DisplayName("Test create transaction functionality")
    public void givenTransactionEntity_whenCreateTransaction_thenNotEnoughCurrencyExceptionThrown() {
        //given
        try (MockedStatic<ReactiveSecurityContextHolder> mockedContextHolder = Mockito.mockStatic(ReactiveSecurityContextHolder.class)) {
            TransactionEntity transactionToSave = TransactionDataUtils.getTransactionTransient();
            transactionToSave.setAmount(10000);

            BDDMockito.given(authentication.getName()).willReturn(MerchantDataUtils.getMerchantDataPersisted().getMerchantName());
            BDDMockito.given(securityContext.getAuthentication()).willReturn(authentication);
            mockedContextHolder.when(ReactiveSecurityContextHolder::getContext).thenReturn(Mono.just(securityContext));

            BDDMockito.given(transactionalOperator.transactional(any(Mono.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));

            BDDMockito.given(customerService.getCustomerByFirstNameLastNameAndCountry(anyString(), anyString(), any(Country.class)))
                    .willReturn(Mono.just(CustomerDataUtils.getCustomerPersistent()));
            BDDMockito.given(accountService.getAccountByUserId(anyLong()))
                    .willReturn(Mono.just(AccountDataUtils.getAccountPersistent()));
            BDDMockito.given(creditCardService.getCreditCardByCardNumber(anyString()))
                    .willReturn(Mono.just(CreditCardDataUtils.getCreditCardDataPersistent()));
            BDDMockito.given(merchantService.getMerchantByMerchantName(anyString()))
                    .willReturn(Mono.just(MerchantDataUtils.getMerchantDataPersisted()));

            //when
            Mono<TransactionEntity> savedTransaction = transactionServiceUnderTest.createTransaction(transactionToSave);
            //then
            StepVerifier.create(savedTransaction)
                            .expectErrorMatches(throwable -> throwable instanceof NotEnoughCurrencyException &&
                                    throwable.getMessage().equals("Insufficient funds for transaction"))
                    .verify();


            verify(merchantService, times(1)).getMerchantByMerchantName(anyString());
            verify(accountService, times(2)).getAccountByUserId(anyLong());
            verify(customerService, times(1)).getCustomerByFirstNameLastNameAndCountry(anyString(), anyString(), any(Country.class));
            verify(creditCardService, times(1)).getCreditCardByCardNumber(anyString());
            verify(transactionRepository, never()).save(any(TransactionEntity.class));
        }
    }
    @Test
    @DisplayName("Test successful transaction functionality")
    public void givenTransactionEntity_whenCreateTransaction_thenFundsTransferredCorrectly() {
        try (MockedStatic<ReactiveSecurityContextHolder> mockedContextHolder = Mockito.mockStatic(ReactiveSecurityContextHolder.class)) {
            TransactionEntity transactionToSave = TransactionDataUtils.getTransactionTransient();

            CustomerEntity customer = CustomerDataUtils.getCustomerPersistent();
            MerchantEntity merchant = MerchantDataUtils.getMerchantDataPersisted();
            AccountEntity customerAccount = AccountDataUtils.getAccountPersistent();
            AccountEntity merchantAccount = AccountDataUtils.getAccountPersistent();
            merchant.getUser().setId(2L);
            merchant.setUserId(2L);
            merchantAccount.setId(2L);
            merchantAccount.setUserId(2L);
            AccountEntity updateCustomerAccount = customerAccount.toBuilder()
                    .amount(customerAccount.getAmount() - transactionToSave.getAmount())
                    .build();
            AccountEntity updatedMerchantAccount = merchantAccount.toBuilder()
                    .amount(merchantAccount.getAmount() + transactionToSave.getAmount())
                    .build();

            BDDMockito.given(authentication.getName()).willReturn(MerchantDataUtils.getMerchantDataPersisted().getMerchantName());
            BDDMockito.given(securityContext.getAuthentication()).willReturn(authentication);
            mockedContextHolder.when(ReactiveSecurityContextHolder::getContext).thenReturn(Mono.just(securityContext));
            BDDMockito.given(transactionalOperator.transactional(any(Mono.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));

            BDDMockito.given(customerService.getCustomerByFirstNameLastNameAndCountry(anyString(), anyString(), any(Country.class)))
                    .willReturn(Mono.just(customer));
            BDDMockito.given(accountService.getAccountByUserId(customerAccount.getId()))
                    .willReturn(Mono.just(customerAccount));
            BDDMockito.given(accountService.getAccountByUserId(merchantAccount.getId()))
                    .willReturn(Mono.just(merchantAccount));
            BDDMockito.given(creditCardService.getCreditCardByCardNumber(anyString()))
                    .willReturn(Mono.just(CreditCardDataUtils.getCreditCardDataPersistent()));
            BDDMockito.given(merchantService.getMerchantByMerchantName(anyString()))
                    .willReturn(Mono.just(merchant));
            BDDMockito.given(accountService.updateAccount(customerAccount))
                    .willReturn(Mono.just(updateCustomerAccount));
            BDDMockito.given(accountService.updateAccount(merchantAccount))
                    .willReturn(Mono.just(updatedMerchantAccount));
            BDDMockito.given(transactionRepository.save(any(TransactionEntity.class)))
                    .willReturn(Mono.just(TransactionDataUtils.getTransactionPersistent()));
            //when
            Mono<TransactionEntity> savedTransaction = transactionServiceUnderTest.createTransaction(transactionToSave);
            //then
            StepVerifier.create(savedTransaction)
                    .assertNext(transaction -> {
                        assertThat(transaction).isNotNull();
                        assertThat(transaction.getCustomer()).isNotNull();
                        assertThat(transaction.getCardData()).isNotNull();
                        assertThat(transaction.getTransactionStatus()).isEqualTo(TransactionStatus.IN_PROGRESS);
                    })
                    .verifyComplete();
            verify(accountService, times(2)).getAccountByUserId(anyLong());
            verify(accountService, times(1)).updateAccount(customerAccount);
            verify(accountService, times(1)).updateAccount(merchantAccount);

            verify(merchantService, times(1)).getMerchantByMerchantName(anyString());
            verify(customerService, times(1)).getCustomerByFirstNameLastNameAndCountry(anyString(), anyString(), any(Country.class));
            verify(creditCardService, times(1)).getCreditCardByCardNumber(anyString());
            verify(transactionRepository, times(1)).save(any(TransactionEntity.class));
        }
    }


    @Test
    @DisplayName("Test get transaction by TransactionID functionality")
    public void given_TransactionId_whenGetTransactionByUUId_thenTransactionIsReturned() {
        //given
        BDDMockito.given(transactionRepository.findTransactionEntityByTransactionId(any(UUID.class)))
                .willReturn(Mono.just(TransactionDataUtils.getTransactionPersistent()));
        BDDMockito.given(customerService.getCustomerById(anyLong()))
                .willReturn(Mono.just(CustomerDataUtils.getCustomerPersistent()));
        BDDMockito.given(creditCardService.getCreditCardById(anyLong()))
                .willReturn(Mono.just(CreditCardDataUtils.getCreditCardDataPersistent()));
        //when
        Mono<TransactionEntity> obtainedTransaction = transactionServiceUnderTest.getTransactionByUUID(TransactionDataUtils.getTransactionPersistent().getTransactionId());

        //then
        StepVerifier.create(obtainedTransaction)
                .assertNext(transaction -> {
                    assertThat(transaction).isNotNull();
                    assertThat(transaction.getCustomer()).isNotNull();
                    assertThat(transaction.getCardData()).isNotNull();
                })
                .verifyComplete();
        verify(transactionRepository, times(1)).findTransactionEntityByTransactionId(any(UUID.class));
        verify(customerService, times(1)).getCustomerById(anyLong());
        verify(creditCardService, times(1)).getCreditCardById(anyLong());

    }

    @Test
    @DisplayName("Test get transaction by TransactionID functionality")
    public void given_TransactionId_whenGetTransactionByUUId_thenExceptionIsThrown() {
        //given
        BDDMockito.given(transactionRepository.findTransactionEntityByTransactionId(any(UUID.class)))
                .willReturn(Mono.empty());

        //when
        Mono<TransactionEntity> obtainedTransaction = transactionServiceUnderTest.getTransactionByUUID(TransactionDataUtils.getTransactionPersistent().getTransactionId());
        //then
        StepVerifier.create(obtainedTransaction)
                .expectErrorMatches(throwable -> throwable instanceof ObjectNotFoundException &&
                        throwable.getMessage().contains("Transaction not found"))
                .verify();
        verify(transactionRepository, times(1)).findTransactionEntityByTransactionId(any(UUID.class));
        verify(customerService, never()).getCustomerById(anyLong());
        verify(creditCardService, never()).getCreditCardById(anyLong());
    }

    @Test
    @DisplayName("Test update transaction functionality")
    public void given_TransactionEntity_whenUpdateTransaction_thenTransactionIsUpdated() {
        TransactionEntity currentTransaction = TransactionDataUtils.getTransactionPersistent();
        currentTransaction.setUpdatedAt(LocalDateTime.now().minusDays(1));
        TransactionEntity transactionToUpdate = currentTransaction.toBuilder()
                .amount(currentTransaction.getAmount() - 2000)
                .build();
        //given
        BDDMockito.given(transactionRepository.findTransactionEntityByTransactionId(any(UUID.class)))
                        .willReturn(Mono.just(transactionToUpdate));
        BDDMockito.given(transactionRepository.save(any(TransactionEntity.class)))
                .willReturn(Mono.just(transactionToUpdate));
        //when

        Mono<TransactionEntity> updatedTransaction = transactionServiceUnderTest.updateTransaction(transactionToUpdate);
        //then

        StepVerifier.create(updatedTransaction)
                .assertNext(transaction -> {
                    assertThat(transaction).isNotNull();
                    assertThat(transaction.getUpdatedAt()).isAfter(currentTransaction.getUpdatedAt());
                    assertThat(transaction.getAmount()).isEqualTo(currentTransaction.getAmount() - 2000);
                })
                .verifyComplete();
        verify(transactionRepository, times(1)).save(any(TransactionEntity.class));
        verify(transactionRepository, times(1)).findTransactionEntityByTransactionId(any(UUID.class));
    }

    @Test
    @DisplayName("Test update transaction functionality")
    public void given_TransactionEntity_whenUpdateTransaction_thenExceptionIsThrown() {
        //given
        TransactionEntity transactionToUpdate = TransactionDataUtils.getTransactionPersistent();

        BDDMockito.given(transactionRepository.findTransactionEntityByTransactionId(any(UUID.class)))
                .willReturn(Mono.empty());
        //when
        Mono<TransactionEntity> updatedTransaction = transactionServiceUnderTest.updateTransaction(transactionToUpdate);
        //then
        StepVerifier.create(updatedTransaction)
                .expectErrorMatches(throwable -> throwable instanceof ObjectNotFoundException &&
                        throwable.getMessage().equals("Transaction not found"))
                .verify();

        verify(transactionRepository, times(1)).findTransactionEntityByTransactionId(any(UUID.class));
        verify(transactionRepository, never()).save(any(TransactionEntity.class));
    }

    @Test
    @DisplayName("Test get transactions for period functionality")
    public void given_StartDateEndDate_whenGetTransactionsForPeriod_thenTransactionsRetrieved() {
        //given
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now();
        List<TransactionEntity> transactionList = Arrays.asList(TransactionDataUtils.getTransactionPersistent(), TransactionDataUtils.getTransactionPersistent(), TransactionDataUtils.getTransactionPersistent());

        BDDMockito.given(transactionRepository.findTransactionEntitiesByCreatedAtBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .willReturn(Flux.fromIterable(transactionList));

        //when
        Flux<TransactionEntity> retrievedTransactions = transactionServiceUnderTest.getTransactionsForPeriod(startDate, endDate);
        //then
        StepVerifier.create(retrievedTransactions)
                .expectNextSequence(transactionList)
                .verifyComplete();

        verify(transactionRepository, times(1)).findTransactionEntitiesByCreatedAtBetween(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Test get transactions for period functionality")
    public void given_StartDateEndDate_whenGetTransactionsForPeriod_thenExceptionIsThrown() {
        //given
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now();

        BDDMockito.given(transactionRepository.findTransactionEntitiesByCreatedAtBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .willReturn(Flux.empty());
        //when
        Flux<TransactionEntity> retrievedTransactions = transactionServiceUnderTest.getTransactionsForPeriod(startDate, endDate);
        //then
        StepVerifier.create(retrievedTransactions)
                .expectErrorMatches(throwable -> throwable instanceof ObjectNotFoundException &&
                        throwable.getMessage().equals("Transaction in period starts with: " + startDate + " and ends with: " + endDate + " not found"))
                .verify();
        verify(transactionRepository, times(1)).findTransactionEntitiesByCreatedAtBetween(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Test get transactions by status functionality")
    public void given_TransactionStatus_whenGetTransactionsByStatus_thenTransactionsRetrieved() {
        //given
        TransactionStatus status = TransactionStatus.IN_PROGRESS;

        BDDMockito.given(transactionRepository.findTransactionEntitiesByTransactionStatus(any(TransactionStatus.class)))
                .willReturn(Flux.just(TransactionDataUtils.getTransactionPersistent()));
        //when
        Flux<TransactionEntity> retrievedTransactions = transactionServiceUnderTest.getAllTransactionsByTransactionStatus(status);
        //then
        StepVerifier.create(retrievedTransactions)
                .assertNext(transaction -> assertThat(transaction).isNotNull())
                .verifyComplete();

        verify(transactionRepository, times(1)).findTransactionEntitiesByTransactionStatus(status);
    }

    @Test
    @DisplayName("Test get transactions by status functionality")
    public void given_TransactionStatus_whenGetTransactionsByStatus_thenExceptionIsThrown() {
        //give
        TransactionStatus status = TransactionStatus.IN_PROGRESS;

        BDDMockito.given(transactionRepository.findTransactionEntitiesByTransactionStatus(status))
                .willReturn(Flux.empty());
        //when
        Flux<TransactionEntity> retrievedTransactions = transactionServiceUnderTest.getAllTransactionsByTransactionStatus(status);
        //then
        StepVerifier.create(retrievedTransactions)
                .expectErrorMatches(throwable -> throwable instanceof ObjectNotFoundException &&
                        throwable.getMessage().equals("No transactions with status: " + status + " found"))
                .verify();

        verify(transactionRepository, times(1)).findTransactionEntitiesByTransactionStatus(status);
    }


}

