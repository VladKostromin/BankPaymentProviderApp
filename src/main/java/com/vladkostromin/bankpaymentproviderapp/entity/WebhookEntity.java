package com.vladkostromin.bankpaymentproviderapp.entity;

import com.vladkostromin.bankpaymentproviderapp.enums.Currency;
import com.vladkostromin.bankpaymentproviderapp.enums.PaymentMethod;
import com.vladkostromin.bankpaymentproviderapp.enums.TransactionStatus;
import com.vladkostromin.bankpaymentproviderapp.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table("webhooks")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class WebhookEntity {

    private Long id;
    private UUID transactionId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private TransactionType transactionType;
    private PaymentMethod paymentMethod;
    private Currency currency;
    private Long cardData;
    private String language;
    private Long customerId;
    private TransactionStatus status;
}
