package com.vladkostromin.bankpaymentproviderapp.controller;

import com.vladkostromin.bankpaymentproviderapp.dto.CreditCardDto;
import com.vladkostromin.bankpaymentproviderapp.dto.TransactionDto;
import com.vladkostromin.bankpaymentproviderapp.entity.CreditCardEntity;
import com.vladkostromin.bankpaymentproviderapp.entity.TransactionEntity;
import com.vladkostromin.bankpaymentproviderapp.mapper.CreditCardMapper;
import com.vladkostromin.bankpaymentproviderapp.mapper.TransactionMapper;
import com.vladkostromin.bankpaymentproviderapp.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final TransactionMapper transactionMapper;
    private final CreditCardMapper creditCardMapper;

    @PostMapping("/transactions")
    public Mono<TransactionDto> createTransaction(@RequestBody TransactionDto transactionDto) {
        TransactionEntity transactionEntity = transactionMapper.map(transactionDto);
        return paymentService.saveTransaction(transactionEntity).map(transactionMapper::map);
    }


}
