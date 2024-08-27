package com.vladkostromin.bankpaymentproviderapp.entity;

import com.vladkostromin.bankpaymentproviderapp.enums.PaymentMethod;
import com.vladkostromin.bankpaymentproviderapp.enums.TransactionStatus;
import com.vladkostromin.bankpaymentproviderapp.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table("transactions")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class TransactionEntity {

    @Id
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
