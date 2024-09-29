package com.vladkostromin.bankpaymentproviderapp.controller;

import com.vladkostromin.bankpaymentproviderapp.dto.TransactionDto;
import com.vladkostromin.bankpaymentproviderapp.dto.response.TransactionResponse;
import com.vladkostromin.bankpaymentproviderapp.entity.TransactionEntity;
import com.vladkostromin.bankpaymentproviderapp.enums.TransactionType;
import com.vladkostromin.bankpaymentproviderapp.mapper.TransactionMapper;
import com.vladkostromin.bankpaymentproviderapp.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;


    @PostMapping("/topups")
    public Mono<TransactionResponse> createTransaction(@RequestBody TransactionDto transactionDto) {
        log.info("IN PaymentController.createTransaction");
        TransactionEntity transactionEntity = transactionMapper.map(transactionDto);
        transactionEntity.setTransactionType(TransactionType.TOP_UP);
        return transactionService.createTransaction(transactionEntity)
                .map(transaction -> TransactionResponse.builder()
                        .transactionId(transaction.getTransactionId())
                        .status(transaction.getTransactionStatus())
                        .message("OK")
                        .build()
        );
    }

    @PostMapping("/payouts")
    public Mono<TransactionResponse> createPayOutTransaction(@RequestBody TransactionDto transactionDto) {
        log.info("IN PaymentController.createPayOutTransaction");
        TransactionEntity transactionEntity = transactionMapper.map(transactionDto);
        transactionEntity.setTransactionType(TransactionType.PAY_OUT);
        return transactionService.createTransaction(transactionEntity)
                .map(transaction -> TransactionResponse.builder()
                        .transactionId(transaction.getTransactionId())
                        .status(transaction.getTransactionStatus())
                        .message("OK")
                        .build()
                );

    }
    @GetMapping("/transaction/list")
    public Flux<TransactionDto> getTransactionList(
            @RequestParam(value = "start_date", required = false) Long startDateTimestamp,
            @RequestParam(value = "end_date", required = false) Long endDateTimestamp) {

        LocalDateTime startDate = (startDateTimestamp != null)
                ? LocalDateTime.ofInstant(Instant.ofEpochSecond(startDateTimestamp), ZoneId.systemDefault())
                : LocalDateTime.now().with(LocalDateTime.MIN);
        LocalDateTime endDate = (endDateTimestamp != null)
                ? LocalDateTime.ofInstant(Instant.ofEpochSecond(endDateTimestamp), ZoneId.systemDefault())
                : LocalDateTime.now().with(LocalDateTime.MAX);
        return transactionService.getTransactionsForPeriod(startDate, endDate)
                .map(transactionMapper::map);
    }
    @GetMapping("/transaction/{transactionId}/details")
    public Mono<TransactionDto> getTransactionDetails(@PathVariable UUID transactionId) {
        log.info("IN PaymentController.getTransactionDetails");
        return transactionService.getTransactionByUUID(transactionId).map(transactionMapper::map);
    }


}
