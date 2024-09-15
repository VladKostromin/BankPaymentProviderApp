package com.vladkostromin.bankpaymentproviderapp.entity;

import com.vladkostromin.bankpaymentproviderapp.enums.Currency;
import com.vladkostromin.bankpaymentproviderapp.enums.PaymentMethod;
import com.vladkostromin.bankpaymentproviderapp.enums.TransactionStatus;
import com.vladkostromin.bankpaymentproviderapp.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
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
    private Long cardData;
    private String language;
    private Long customerId;
    private TransactionStatus status;
}
