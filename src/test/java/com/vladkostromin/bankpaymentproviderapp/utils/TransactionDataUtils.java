package com.vladkostromin.bankpaymentproviderapp.utils;

import com.vladkostromin.bankpaymentproviderapp.dto.TransactionDto;
import com.vladkostromin.bankpaymentproviderapp.entity.TransactionEntity;
import com.vladkostromin.bankpaymentproviderapp.enums.Currency;
import com.vladkostromin.bankpaymentproviderapp.enums.PaymentMethod;
import com.vladkostromin.bankpaymentproviderapp.enums.TransactionStatus;
import com.vladkostromin.bankpaymentproviderapp.enums.TransactionType;

import java.time.LocalDateTime;
import java.util.UUID;

public class TransactionDataUtils {

    private final static UUID transactionId = UUID.randomUUID();

    public static TransactionEntity getTransactionTransient() {
        return TransactionEntity.builder()
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .transactionType(TransactionType.TOP_UP)
                .amount(5000)
                .currency(Currency.BRL)
                .cardData(CreditCardDataUtils.getCreditCardDataTransient())
                .language("en")
                .notificationUrl("https://testnotification.net/webhook/transaction")
                .customer(CustomerDataUtils.getCustomerTransient())
                .build();
    }

    public static TransactionEntity getTransactionPersistent() {
        return TransactionEntity.builder()
                .id(1L)
                .transactionId(transactionId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .transactionType(TransactionType.TOP_UP)
                .transactionId(transactionId)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .amount(5000)
                .currency(Currency.BRL)
                .cardId(CreditCardDataUtils.getCreditCardDataPersistent().getId())
                .cardData(CreditCardDataUtils.getCreditCardDataPersistent())
                .language("en")
                .notificationUrl("https://testnotification.net/webhook/transaction")
                .customerId(CustomerDataUtils.getCustomerPersistent().getId())
                .customer(CustomerDataUtils.getCustomerPersistent())
                .transactionStatus(TransactionStatus.IN_PROGRESS)
                .accountFrom(AccountDataUtils.getAccountPersistent().getId())
                .accountTo(AccountDataUtils.getAccountPersistent().getId())
                .build();
    }

    public static TransactionDto getTransactionTransientDto() {
        return TransactionDto.builder()
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .transactionType(TransactionType.TOP_UP)
                .amount(5000)
                .currency(Currency.BRL)
                .cardData(CreditCardDataUtils.getCreditCardDataDtoTransient())
                .language("en")
                .notificationUrl("https://testnotification.net/webhook/transaction")
                .customer(CustomerDataUtils.getCustomerDtoTransient())
                .build();
    }
}
