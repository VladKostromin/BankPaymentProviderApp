package com.vladkostromin.bankpaymentproviderapp.it;


import com.vladkostromin.bankpaymentproviderapp.config.PostgresSQLTestcontainersConfig;
import com.vladkostromin.bankpaymentproviderapp.dto.MerchantDto;
import com.vladkostromin.bankpaymentproviderapp.repository.MerchantRepository;
import com.vladkostromin.bankpaymentproviderapp.service.MerchantService;
import com.vladkostromin.bankpaymentproviderapp.utils.MerchantDataUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Import(PostgresSQLTestcontainersConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class MerchantRestControllerV1Tests {

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private WebTestClient webTestClient;

    private MerchantService merchantService;

    @BeforeEach
    public void setUp() {
        merchantRepository.deleteAll().block();
    }


    @Test
    @DisplayName("Test create merchant functionality")
    public void givenMerchantDto_whenCreateMerchant_thenMerchantIsCreated() {
        //given
        MerchantDto merchantDto = MerchantDataUtils.getMerchantDataDtoTransient();
        //when
        WebTestClient.ResponseSpec result  = webTestClient.post()
                .uri("/api/v1/merchants/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(merchantDto), MerchantDto.class)
                .exchange();
        //then
        result.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.merchant_name").isEqualTo(merchantDto.getMerchantName());
    }
}
