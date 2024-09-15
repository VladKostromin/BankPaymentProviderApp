package com.vladkostromin.bankpaymentproviderapp.service;


import com.vladkostromin.bankpaymentproviderapp.entity.AccountEntity;
import com.vladkostromin.bankpaymentproviderapp.entity.CreditCardEntity;
import com.vladkostromin.bankpaymentproviderapp.exceptions.ObjectNotFoundException;
import com.vladkostromin.bankpaymentproviderapp.repository.CreditCardRepository;
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
public class CreditCardServiceTests {

    @Mock
    private CreditCardRepository creditCardRepository;

    @InjectMocks
    private CreditCardService creditCardService;

    private AutoCloseable autoCloseable;

    @BeforeEach
    public void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);

        creditCardService = new CreditCardService(creditCardRepository);
    }

    @AfterEach
    public void tearDown() throws Exception {
        autoCloseable.close();
    }


    @Test
    @DisplayName("Test create credit card functionality")
    public void givenCreditCardAndAccount_whenCreateCreditCard_thenCardIsCreatedWithAccountId() {
        //given
        CreditCardEntity creditCardToCreate = CreditCardDataUtils.getCreditCardDataTransient();
        AccountEntity accountForCreditCard = AccountDataUtils.getAccountPersistent();
        creditCardToCreate.setAccountId(accountForCreditCard.getId());

        BDDMockito.given(creditCardRepository.save(any(CreditCardEntity.class)))
                .willReturn(Mono.just(CreditCardDataUtils.getCreditCardDataPersistent()));
        //when
        Mono<CreditCardEntity> createdCreditCard = creditCardService.createCreditCard(creditCardToCreate, accountForCreditCard);
        //then
        StepVerifier.create(createdCreditCard)
                .assertNext(creditCard -> {
                    assertThat(creditCard).isNotNull();
                    assertThat(creditCard.getAccountId()).isEqualTo(accountForCreditCard.getId());
                })
                .verifyComplete();
        verify(creditCardRepository, times(1)).save(any(CreditCardEntity.class));
    }

    @Test
    @DisplayName("Test get credit card by id functionality")
    public void givenCreditCardId_whenGetCreditCardById_thenCardIsRetrieved() {
        //given
        Long id = 1L;
        BDDMockito.given(creditCardRepository.findById(anyLong()))
                .willReturn(Mono.just(CreditCardDataUtils.getCreditCardDataPersistent()));
        //when
        Mono<CreditCardEntity> retrievedCreditCard = creditCardService.getCreditCardById(id);
        //then
        StepVerifier.create(retrievedCreditCard)
                .assertNext(creditCard -> {
                    assertThat(creditCard).isNotNull();
                    assertThat(creditCard.getId()).isEqualTo(id);
                })
                .verifyComplete();
        verify(creditCardRepository, times(1)).findById(anyLong());

    }
    @Test
    @DisplayName("Test get credit card by id throws exception functionality")
    public void givenCreditCardId_thenGetCreditCardById_thenExceptionIsThrown() {
        //given
        Long id = 1L;
        BDDMockito.given(creditCardRepository.findById(anyLong()))
                .willReturn(Mono.empty());
        //when
        Mono<CreditCardEntity> retrievedCreditCard = creditCardService.getCreditCardById(id);
        //then
        StepVerifier.create(retrievedCreditCard)
                .expectErrorMatches(throwable -> throwable instanceof ObjectNotFoundException &&
                        throwable.getMessage().contains("Credit card not found"))
                .verify();
        verify(creditCardRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("Test update credit card functionality")
    public void givenCreditCard_whenUpdateCreditCard_thenCreditCardIsUpdated() {
        //given
        CreditCardEntity currentCreditCard = CreditCardDataUtils.getCreditCardDataPersistent();
        CreditCardEntity creditCardToUpdate = currentCreditCard.toBuilder()
                .updatedAt(LocalDateTime.now().plusMinutes(1))
                .accountId(2L)
                .build();
        BDDMockito.given(creditCardRepository.findById(anyLong()))
                        .willReturn(Mono.just(currentCreditCard));
        BDDMockito.given(creditCardRepository.save(any(CreditCardEntity.class)))
                .willReturn(Mono.just(creditCardToUpdate));
        //when
        Mono<CreditCardEntity> updateCreditCard = creditCardService.updateCreditCard(currentCreditCard);
        //then
        StepVerifier.create(updateCreditCard)
                .assertNext(creditCard -> {
                    assertThat(creditCard).isNotNull();
                    assertThat(creditCard.getAccountId()).isNotEqualByComparingTo(currentCreditCard.getAccountId());
                    assertThat(creditCard.getAccountId()).isEqualTo(2L);
                    assertThat(creditCard.getUpdatedAt()).isAfter(currentCreditCard.getUpdatedAt());
                })
                .verifyComplete();
        verify(creditCardRepository, times(1)).findById(anyLong());
        verify(creditCardRepository, times(1)).save(any(CreditCardEntity.class));
    }
    @Test
    @DisplayName("Test update credit card functionality")
    public void givenCreditCard_whenUpdateCreditCard_thenExceptionIsThrown() {
        //given
        CreditCardEntity creditCardToUpdate = CreditCardDataUtils.getCreditCardDataPersistent();
        BDDMockito.given(creditCardRepository.findById(anyLong()))
                .willReturn(Mono.empty());
        //when
        Mono<CreditCardEntity> retrievedCreditCard = creditCardService.updateCreditCard(creditCardToUpdate);
        //then
        StepVerifier.create(retrievedCreditCard)
                .expectErrorMatches(throwable -> throwable instanceof ObjectNotFoundException &&
                        throwable.getMessage().contains("Credit card not found"))
                .verify();
        verify(creditCardRepository, times(1)).findById(anyLong());
        verify(creditCardRepository, never()).save(any(CreditCardEntity.class));
    }

    @Test
    @DisplayName("Test get credit card by card number functionality")
    public void givenCreditCardNumber_thenGetCreditCardByCardNumber_thenCreditCardIsRetrieved() {
        //given
        String cardNumber = CreditCardDataUtils.getCreditCardDataTransient().getCardNumber();
        BDDMockito.given(creditCardRepository.findByCardNumber(anyString()))
                .willReturn(Mono.just(CreditCardDataUtils.getCreditCardDataPersistent()));
        //when
        Mono<CreditCardEntity> retrievedCreditCard = creditCardService.getCreditCardByCardNumber(cardNumber);
        //then
        StepVerifier.create(retrievedCreditCard)
                .assertNext(creditCard -> {
                    assertThat(creditCard.getId()).isNotNull();
                    assertThat(creditCard.getCardNumber()).isEqualTo(cardNumber);
                })
                .verifyComplete();
        verify(creditCardRepository, times(1)).findByCardNumber(anyString());
    }

    @Test
    @DisplayName("Test get credit card by card number functionality")
    public void givenCreditCardNumber_thenGetCreditCardByCardNumber_thenExceptionIsThrown() {
        //given
        String cardNumber = CreditCardDataUtils.getCreditCardDataPersistent().getCardNumber();
        BDDMockito.given(creditCardRepository.findByCardNumber(anyString()))
                .willReturn(Mono.empty());
        //when
        Mono<CreditCardEntity> retrievedCreditCard = creditCardService.getCreditCardByCardNumber(cardNumber);
        //then

        StepVerifier.create(retrievedCreditCard)
                .expectErrorMatches(throwable -> throwable instanceof ObjectNotFoundException &&
                        throwable.getMessage().contains("Credit card not found"))
                .verify();
        verify(creditCardRepository, times(1)).findByCardNumber(anyString());
    }

    @Test
    @DisplayName("Test get all credit cards by accountId functionality")
    public void givenAccountId_whenGetAllCreditCards_thenCardsAreReturned() {
        // given
        Long accountId = 1L;
        List<CreditCardEntity> creditCards = CreditCardDataUtils.getCreditCardDataList();
        BDDMockito.given(creditCardRepository.findAllByAccountId(accountId))
                .willReturn(Flux.fromIterable(creditCards));

        // when
        Flux<CreditCardEntity> retrievedCards = creditCardService.getAllCreditCardsByAccountId(accountId);

        // then
        StepVerifier.create(retrievedCards)
                .expectNextSequence(creditCards)
                .verifyComplete();

        verify(creditCardRepository, times(1)).findAllByAccountId(accountId);
    }

    @Test
    @DisplayName("Test get all credit cards by accountId functionality")
    public void givenAccountId_whenGetAllCreditCardsByAccountId_thenExceptionIsThrown() {
        // given
        Long accountId = CreditCardDataUtils.getCreditCardDataPersistent().getAccountId();
        BDDMockito.given(creditCardRepository.findAllByAccountId(accountId))
                .willReturn(Flux.empty());

        // when
        Flux<CreditCardEntity> retrievedCards = creditCardService.getAllCreditCardsByAccountId(accountId);

        // then
        StepVerifier.create(retrievedCards)
                .expectErrorMatches(throwable -> throwable instanceof ObjectNotFoundException &&
                        throwable.getMessage().equals("Credit cards not found"))
                .verify();

        verify(creditCardRepository, times(1)).findAllByAccountId(accountId);
    }





}
