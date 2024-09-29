package com.vladkostromin.bankpaymentproviderapp.controller;

import com.vladkostromin.bankpaymentproviderapp.dto.MerchantDto;
import com.vladkostromin.bankpaymentproviderapp.entity.MerchantEntity;
import com.vladkostromin.bankpaymentproviderapp.mapper.MerchantMapper;
import com.vladkostromin.bankpaymentproviderapp.service.AccountService;
import com.vladkostromin.bankpaymentproviderapp.service.MerchantService;
import com.vladkostromin.bankpaymentproviderapp.service.UserService;
import com.vladkostromin.bankpaymentproviderapp.utils.AccountDataUtils;
import com.vladkostromin.bankpaymentproviderapp.utils.MerchantDataUtils;
import com.vladkostromin.bankpaymentproviderapp.utils.UserDataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;

@ComponentScan({"com.vladkostromin.bankpaymentproviderapp.exceptions.errorhandler"})
@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = {MerchantController.class})
public class MerchantRestControllerV1Tests {

    @Autowired
    private WebTestClient webClient;
    @MockBean
    private MerchantMapper merchantMapper;
    @MockBean
    private UserService userService;
    @MockBean
    private AccountService accountService;
    @MockBean
    private MerchantService merchantService;

    @Test
    @DisplayName("Test create merchant functionality")
    @WithMockUser
    public void givenMerchantDto_whenCreateMerchant_thenSuccessResponse() {
        //given
        MerchantDto merchantDto = MerchantDataUtils.getMerchantDataDtoTransient();
        MerchantEntity merchantEntity = MerchantDataUtils.getMerchantDataPersisted();

        BDDMockito.given(merchantService.saveMerchant(any(MerchantEntity.class)))
                .willReturn(Mono.just(merchantEntity));
        BDDMockito.given(merchantMapper.map(any(MerchantDto.class)))
                .willReturn(merchantEntity);
        BDDMockito.given(merchantMapper.map(any(MerchantEntity.class)))
                .willReturn(merchantDto);
        BDDMockito.given(userService.createUser())
                .willReturn(Mono.just(UserDataUtils.getUserData()));
        BDDMockito.given(accountService.createAccount(merchantEntity.getUserId()))
                .willReturn(Mono.just(AccountDataUtils.getAccountPersistent()));
        //when
        WebTestClient.ResponseSpec result = webClient
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .post()
                .uri("/api/v1/merchants/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(merchantDto), MerchantDto.class)
                .exchange();
        //then
        result.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.merchant_name").isEqualTo(merchantDto.getMerchantName())
                .jsonPath("$.password").isNotEmpty();
    }
}
