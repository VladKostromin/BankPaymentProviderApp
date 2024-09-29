package com.vladkostromin.bankpaymentproviderapp.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.vladkostromin.bankpaymentproviderapp.enums.Currency;
import com.vladkostromin.bankpaymentproviderapp.enums.PaymentMethod;
import com.vladkostromin.bankpaymentproviderapp.enums.TransactionStatus;
import com.vladkostromin.bankpaymentproviderapp.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionDto extends BaseDto {


    private UUID transactionId;
    private TransactionType transactionType;
    private PaymentMethod paymentMethod;
    private Integer amount;
    private Currency currency;
    private String language;
    private String notificationUrl;
    private TransactionStatus transactionStatus;
    private CreditCardDto cardData;
    private CustomerDto customer;

    @JsonProperty
    @Override
    public LocalDateTime getCreatedAt() {
        return super.getCreatedAt();
    }

    @JsonProperty
    @Override
    public LocalDateTime getUpdatedAt() {
        return super.getUpdatedAt();
    }
}
