package com.vladkostromin.bankpaymentproviderapp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Table;

@Table("credit_cards")
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class CreditCardEntity extends BaseEntity {

    private Long accountId;
    private String cardNumber;
    private String expDate;
    private String cvv;
}
