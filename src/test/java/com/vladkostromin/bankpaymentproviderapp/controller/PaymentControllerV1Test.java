package com.vladkostromin.bankpaymentproviderapp.controller;

import com.vladkostromin.bankpaymentproviderapp.dto.TransactionDto;
import com.vladkostromin.bankpaymentproviderapp.dto.response.TransactionResponse;
import com.vladkostromin.bankpaymentproviderapp.entity.AccountEntity;
import com.vladkostromin.bankpaymentproviderapp.entity.TransactionEntity;
import com.vladkostromin.bankpaymentproviderapp.enums.Country;
import com.vladkostromin.bankpaymentproviderapp.enums.TransactionStatus;
import com.vladkostromin.bankpaymentproviderapp.mapper.TransactionMapper;
import com.vladkostromin.bankpaymentproviderapp.service.*;
import com.vladkostromin.bankpaymentproviderapp.utils.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyString;

@ComponentScan({"com.vladkostromin.bankpaymentproviderapp.exceptions.errorhandler"})
@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = {PaymentController.class})
public class PaymentControllerV1Test {

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private TransactionService transactionService;
    @MockBean
    private TransactionMapper transactionMapper;
    @MockBean
    private Authentication authentication;
    @MockBean
    private SecurityContext securityContext;
    @MockBean
    private TransactionalOperator transactionalOperator;
    @MockBean
    private CustomerService customerService;
    @MockBean
    private AccountService accountService;
    @MockBean
    private CreditCardService creditCardService;
    @MockBean
    private MerchantService merchantService;

    @Test
    @DisplayName("Test create transaction functionality")
    @WithMockUser
    public void givenTransactionDto_whenCreateTopUpTransaction_thenSuccessResponse() {
        //given
        try(MockedStatic<ReactiveSecurityContextHolder> mockedContextHolder = Mockito.mockStatic(ReactiveSecurityContextHolder.class)) {
            BDDMockito.given(authentication.getName()).willReturn(MerchantDataUtils.getMerchantDataPersisted().getMerchantName());
            BDDMockito.given(securityContext.getAuthentication()).willReturn(authentication);
            mockedContextHolder.when(ReactiveSecurityContextHolder::getContext).thenReturn(Mono.just(securityContext));

            BDDMockito.given(transactionalOperator.transactional(any(Mono.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));

            TransactionDto transactionDto = TransactionDataUtils.getTransactionTransientDto();
            TransactionEntity transactionEntity = TransactionDataUtils.getTransactionPersistent();
            TransactionResponse expectedResponse = TransactionResponse.builder()
                    .transactionId(transactionEntity.getTransactionId())
                    .message("OK")
                    .status(TransactionStatus.IN_PROGRESS)
                    .build();

            BDDMockito.given(customerService.getCustomerByFirstNameLastNameAndCountry(anyString(), anyString(), any(Country.class)))
                    .willReturn(Mono.just(CustomerDataUtils.getCustomerPersistent()));
            BDDMockito.given(accountService.getAccountByUserId(anyLong()))
                    .willReturn(Mono.just(AccountDataUtils.getAccountPersistent()));
            BDDMockito.given(creditCardService.getCreditCardByCardNumber(anyString()))
                    .willReturn(Mono.just(CreditCardDataUtils.getCreditCardDataPersistent()));
            BDDMockito.given(merchantService.getMerchantByMerchantName(anyString()))
                    .willReturn(Mono.just(MerchantDataUtils.getMerchantDataPersisted()));
            BDDMockito.given(accountService.updateAccount(any(AccountEntity.class)))
                    .willReturn(Mono.just(AccountDataUtils.getAccountPersistent()));
            BDDMockito.given(transactionMapper.map(transactionDto))
                    .willReturn(transactionEntity);
            BDDMockito.given(transactionMapper.map(transactionEntity))
                    .willReturn(transactionDto);
            BDDMockito.given(transactionService.createTransaction(any(TransactionEntity.class)))
                    .willReturn(Mono.just(transactionEntity));

            BDDMockito.given(transactionMapper.map(transactionDto))
                    .willReturn(transactionEntity);
            BDDMockito.given(transactionMapper.map(transactionEntity))
                    .willReturn(transactionDto);
            //when
            WebTestClient.ResponseSpec result = webClient
                    .mutateWith(SecurityMockServerConfigurers.csrf())
                    .post()
                    .uri("/api/v1/payments/topups")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Mono.just(transactionDto), TransactionDto.class)
                    .exchange();
            //then

            result.expectStatus().isOk()
                    .expectBody()
                    .consumeWith(System.out::println)
                    .jsonPath("$.transactionId").isEqualTo(expectedResponse.getTransactionId())
                    .jsonPath("$.message").isEqualTo("OK")
                    .jsonPath("$.status").isEqualTo("IN_PROGRESS");
        }

    }

    @Test
    @DisplayName("Test create transaction functionality")
    public void givenTransactionDto_whenCreateTopUpTransaction_thenNotEnoughCurrencyResponse() {
        //given
        //when
        //then
    }
}
