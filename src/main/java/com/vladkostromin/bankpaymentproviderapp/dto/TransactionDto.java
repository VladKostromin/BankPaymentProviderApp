package com.vladkostromin.bankpaymentproviderapp.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.vladkostromin.bankpaymentproviderapp.enums.PaymentMethod;
import com.vladkostromin.bankpaymentproviderapp.enums.TransactionStatus;
import com.vladkostromin.bankpaymentproviderapp.enums.TransactionType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionDto {

    private UUID transactionId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private TransactionType transactionType;
    private PaymentMethod paymentMethod;
    private Integer amount;
    private String currency;
    private String language;
    private String notificationUrl;
    private TransactionStatus transactionStatus;
}
