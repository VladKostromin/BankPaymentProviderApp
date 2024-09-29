package com.vladkostromin.bankpaymentproviderapp.controller.externalcontrollers;

import com.vladkostromin.bankpaymentproviderapp.annotation.RandomNumber;
import com.vladkostromin.bankpaymentproviderapp.entity.TransactionEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Random;

@RestController
@RequestMapping("/api/bank-api")
@Slf4j
public class BankApiController {

    @RandomNumber(bound = 100)
    private int randomNumber;

    @PostMapping("/process-transaction")
    public Mono<ResponseEntity<TransactionEntity>> processTransaction(@RequestBody TransactionEntity transaction) {
        Random random = new Random();
        if(random.nextInt(100) < 80) {
            log.info(transaction.toString());
            return Mono.just(ResponseEntity.ok(transaction));
        }
        else {
            log.info(HttpStatus.INTERNAL_SERVER_ERROR.toString());
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(transaction));
        }

    }
}
