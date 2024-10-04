package com.vladkostromin.bankpaymentproviderapp.service;

import com.vladkostromin.bankpaymentproviderapp.entity.TransactionEntity;
import com.vladkostromin.bankpaymentproviderapp.entity.WebhookEntity;
import com.vladkostromin.bankpaymentproviderapp.enums.WebHookStatus;
import com.vladkostromin.bankpaymentproviderapp.repository.WebHookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebHookService {

    private final WebHookRepository webHookRepository;
    private final WebClient webClient;



    public Mono<Void> sendWebHook(TransactionEntity transaction) {
        return sendWebHookWithRetries(transaction, 5);
    }

    private Mono<Void> sendWebHookWithRetries(TransactionEntity transaction, int retriesLeft) {
        if (retriesLeft == 0) {
            log.error("Max retries reached for transaction: {}", transaction.getTransactionId());
            return saveWebHook(transaction, WebHookStatus.FAILED, "Max retries reached");
        }

        return webClient.post()
                .uri(transaction.getNotificationUrl())
                .bodyValue(transaction)
                .retrieve()
                .toBodilessEntity()
                .flatMap(responseEntity -> {
                    log.info("Webhook successful for transaction: {}", transaction.getTransactionId());
                    return saveWebHook(transaction, WebHookStatus.SENT, "Webhook sent successfully");
                })
                .onErrorResume(error -> {
                    log.error("Error occurred while sending webhook: {}, retries left: {}", error.getMessage(), retriesLeft - 1);
                    return saveWebHook(transaction, WebHookStatus.FAILED,
                            "Error sending webhook: " + error.getMessage())
                            .then(Mono.delay(Duration.ofSeconds(10)))
                            .then(sendWebHookWithRetries(transaction, retriesLeft - 1));
                });
    }

    private Mono<Void> saveWebHook(TransactionEntity transaction, WebHookStatus status, String message) {
        return webHookRepository.save(WebhookEntity.builder()
                        .transactionId(transaction.getTransactionId())
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .paymentMethod(transaction.getPaymentMethod())
                        .currency(transaction.getCurrency())
                        .language(transaction.getLanguage())
                        .transactionType(transaction.getTransactionType())
                        .customerId(transaction.getCustomerId())
                        .cardId(transaction.getCardId())
                        .creditCard(transaction.getCardData())
                        .customer(transaction.getCustomer())
                        .message(message)
                        .webHookStatus(status)
                        .transactionStatus(transaction.getTransactionStatus())
                .build()).then();
    }

}
