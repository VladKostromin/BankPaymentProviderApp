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
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("transactions")
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class TransactionEntity extends BaseEntity {

    private UUID transactionId;
    private TransactionType transactionType;
    private PaymentMethod paymentMethod;
    private Integer amount;
    private Currency currency;
    private Long cardId;
    private Long customerId;
    private String language;
    private String notificationUrl;
    private TransactionStatus transactionStatus;
    private Long accountFrom;
    private Long accountTo;


    @Transient
    private CreditCardEntity cardData;
    @Transient
    private CustomerEntity customer;
}
