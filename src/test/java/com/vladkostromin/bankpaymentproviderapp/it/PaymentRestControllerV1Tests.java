package com.vladkostromin.bankpaymentproviderapp.it;

import com.vladkostromin.bankpaymentproviderapp.config.PostgresSQLTestcontainersConfig;
import com.vladkostromin.bankpaymentproviderapp.dto.TransactionDto;
import com.vladkostromin.bankpaymentproviderapp.dto.response.TransactionResponse;
import com.vladkostromin.bankpaymentproviderapp.entity.MerchantEntity;
import com.vladkostromin.bankpaymentproviderapp.entity.TransactionEntity;
import com.vladkostromin.bankpaymentproviderapp.enums.TransactionType;
import com.vladkostromin.bankpaymentproviderapp.repository.TransactionRepository;
import com.vladkostromin.bankpaymentproviderapp.service.MerchantService;
import com.vladkostromin.bankpaymentproviderapp.utils.MerchantDataUtils;
import com.vladkostromin.bankpaymentproviderapp.utils.TransactionDataUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Import(PostgresSQLTestcontainersConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class PaymentRestControllerV1Tests {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private MerchantService merchantService;

    private final static String basicAuthHeader = getBasicAuthHeader(MerchantDataUtils.getMerchantDataTransient().getMerchantName(), MerchantDataUtils.getMerchantDataTransient().getPassword());

    @BeforeEach
    public void setUp() {
        MerchantEntity merchant = MerchantDataUtils.getMerchantDataTransient();
        transactionRepository.deleteAll().block();
        merchantService.deleteAllMerchants().block();
        merchantService.saveMerchant(merchant).block();

    }
    @Test
    @DisplayName("Test create transaction functionality")
    public void givenTransactionDto_whenCreateTopUpTransaction_thenTransactionSuccessResponse() {
        //given
        TransactionDto transactionDto = TransactionDataUtils.getTransactionTransientDto();
        //when
        WebTestClient.ResponseSpec result = webTestClient.post()
                .uri("/api/v1/payments/topups")
                .header(HttpHeaders.AUTHORIZATION, basicAuthHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(transactionDto), TransactionDto.class)
                .exchange();
        //then
        result.expectStatus().isOk()
                .expectBody()
                .jsonPath("$.transaction_id").isNotEmpty()
                .jsonPath("$.status").isEqualTo("IN_PROGRESS")
                .jsonPath("$.message").isEqualTo("OK");
    }

    @Test
    @DisplayName("Test create transaction functionality")
    public void givenTransactionDto_whenCreatePayOutTransaction_thenTransactionSuccessResponse() {
        //given
        TransactionDto transactionDto = TransactionDataUtils.getTransactionTransientDto();
        transactionDto.setTransactionType(TransactionType.PAY_OUT);
        //when
        WebTestClient.ResponseSpec result = webTestClient.post()
                .uri("/api/v1/payments/payouts")
                .header(HttpHeaders.AUTHORIZATION, basicAuthHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(transactionDto), TransactionDto.class)
                .exchange();
        //then
        result.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.transaction_id").isNotEmpty()
                .jsonPath("$.status").isEqualTo("IN_PROGRESS")
                .jsonPath("$.message").isEqualTo("OK");
    }

    @Test
    @DisplayName("Test create transaction functionality")
    public void givenTransactionDto_whenCreateTransaction_thenNotEnoughCurrencyResponse() {
        //given
        TransactionDto transactionDto = TransactionDataUtils.getTransactionTransientDto();
        transactionDto.setAmount(10000);
        //when
        WebTestClient.ResponseSpec result = webTestClient.post()
                .uri("/api/v1/payments/topups")
                .header(HttpHeaders.AUTHORIZATION, basicAuthHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(transactionDto), TransactionDto.class)
                .exchange();
        //then
        result.expectStatus().isBadRequest()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.errors[0].code").isEqualTo("ERROR_CODE_NOT_ENOUGH_CURRENCY")
                .jsonPath("$.errors[0].message").isEqualTo("Insufficient funds for transaction");

    }

    @Test
    @DisplayName("Test get transaction list within date functionality")
    public void givenTransactionTimeRange_whenGetTransactionListWithinDate_thenTransactionSuccessResponse() {
        //given
        TransactionDto transaction = TransactionDataUtils.getTransactionTransientDto();


        webTestClient.post()
                .uri("/api/v1/payments/payouts")
                .header(HttpHeaders.AUTHORIZATION, basicAuthHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(transaction), TransactionDto.class)
                .exchange();

        Long startDateTimestamp = LocalDateTime.now().minusDays(1).atZone(ZoneId.systemDefault()).toEpochSecond();
        Long endDateTimestamp = LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toEpochSecond();

        List<TransactionEntity> list = transactionRepository.findAll().collectList().block();
        System.out.println(list);
        //when
        WebTestClient.ResponseSpec result = webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/payments/transaction/list")
                        .queryParam("start_date", startDateTimestamp)
                        .queryParam("end_date", endDateTimestamp)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, basicAuthHeader)
                .exchange();
        //then
        result.expectStatus().isOk()
                .expectBodyList(TransactionDto.class)
                .hasSize(1);
    }

    @Test
    @DisplayName("Test create transaction functionality")
    public void given_TransactionDto_whenCreateTransaction_thenExceptionResponse() {

    }

    @Test
    @DisplayName("Test get transaction details functionality")
    public void givenTransactionId_whenGetTransactionDetails_thenTransactionSuccessResponse() {
        //given
        TransactionDto transaction = TransactionDataUtils.getTransactionTransientDto();
        TransactionResponse transactionResponse;

        WebTestClient.ResponseSpec exchange = webTestClient.post()
                .uri("/api/v1/payments/topups")
                .header(HttpHeaders.AUTHORIZATION, basicAuthHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(transaction), TransactionDto.class)
                .exchange();
        String transactionId = Objects.requireNonNull(exchange
                        .expectBody(TransactionResponse.class)
                        .returnResult()
                        .getResponseBody())
                .getTransactionId().toString();
        //when
        WebTestClient.ResponseSpec result = webTestClient.get()
                .uri("/api/v1/payments/transaction/{transactionId}/details", transactionId)
                .header(HttpHeaders.AUTHORIZATION, basicAuthHeader)
                .exchange();
        //then
        result.expectStatus().isOk()
                .expectBody(TransactionDto.class)
                .consumeWith(response -> {
                    TransactionDto responseBody = response.getResponseBody();
                    assertNotNull(responseBody);
                    assertEquals(transaction.getAmount(), responseBody.getAmount());
                    assertEquals(transaction.getCurrency(), responseBody.getCurrency());
                    assertEquals(transaction.getPaymentMethod(), responseBody.getPaymentMethod());
                });
    }

    private static String getBasicAuthHeader(String username, String password) {
        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedAuth);
    }
}
