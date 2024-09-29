package com.vladkostromin.bankpaymentproviderapp.entity;

import com.vladkostromin.bankpaymentproviderapp.enums.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("webhooks")
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class WebhookEntity extends BaseEntity {

    private UUID transactionId;
    private TransactionType transactionType;
    private PaymentMethod paymentMethod;
    private Currency currency;
    private String language;
    private Long customerId;
    private Long cardId;
    private WebHookStatus webHookStatus;
    private String message;

    @Transient
    private CustomerEntity customer;
    @Transient
    private CreditCardEntity creditCard;
}
