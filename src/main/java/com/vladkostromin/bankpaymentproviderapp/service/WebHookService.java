package com.vladkostromin.bankpaymentproviderapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladkostromin.bankpaymentproviderapp.entity.TransactionEntity;
import com.vladkostromin.bankpaymentproviderapp.entity.WebhookEntity;
import com.vladkostromin.bankpaymentproviderapp.exceptions.ApiException;
import com.vladkostromin.bankpaymentproviderapp.repository.WebHookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebHookService {

    private final WebClient webClient;
    private final WebHookRepository webHookRepository;
    private final ObjectMapper objectMapper;

    private static final Integer MAX_RETRY_ATTEMPTS = 5;
    private static final Duration RETRY_INTERVAL = Duration.ofSeconds(10);


//    public Mono<Void> sendNotification(TransactionEntity transaction) {
//        return processNotification(transaction, 1);
//    }

//    private Mono<Void> processNotification(TransactionEntity transaction, Integer attempt){
//        return webClient.post()
//                .uri(transaction.getNotificationUrl())
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(webHookToJsonSerialization(transaction))
//                .retrieve()
//                .toBodilessEntity()
//                .flatMap(response -> {
//                    if (response.getStatusCode().is2xxSuccessful()) {
//                        log.info("IN savingWebHook successful with status code {}", response.getStatusCode());
//                        return saveWebHook(transaction);
//                    }
//
//                });
//    }



    public Mono<WebhookEntity> createNotification(TransactionEntity transaction) {
        return webHookRepository.save(WebhookEntity.builder().build());
    }

    public Mono<WebhookEntity> updateNotification(WebhookEntity webhook) {
        return webHookRepository.save(webhook);
    }

    public Mono<Void> deleteNotification(WebhookEntity webhook) {
        return webHookRepository.delete(webhook);
    }

    private String webHookToJsonSerialization(TransactionEntity transaction) {
        try {
            return objectMapper.writeValueAsString(transaction);
        } catch (Exception e) {
            throw new ApiException("Failed to serialize transaction", "ERROR_CODE_JSON_SERIALIZATION");
        }
    }

}
