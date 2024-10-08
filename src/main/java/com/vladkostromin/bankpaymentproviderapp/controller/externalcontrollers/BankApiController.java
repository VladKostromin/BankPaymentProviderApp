package com.vladkostromin.bankpaymentproviderapp.controller.externalcontrollers;

import com.vladkostromin.bankpaymentproviderapp.entity.TransactionEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Random;

@RestController
@RequestMapping("/api/bank-api")
@Slf4j
public class BankApiController {


    @PostMapping("/process-transaction")
    public Mono<ResponseEntity<TransactionEntity>> processTransaction(@RequestBody TransactionEntity transaction) {
        Random random = new Random();
        return Mono.delay(Duration.ofSeconds(2))
                .flatMap(delay -> {
                    if (random.nextInt(100) < 80) {
                        log.info("Transaction processed successfully: {}", transaction.toString());
                        return Mono.just(ResponseEntity.ok(transaction));
                    } else {
                        log.info("Transaction failed: {}", HttpStatus.INTERNAL_SERVER_ERROR);
                        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(transaction));
                    }
                });
    }
}
