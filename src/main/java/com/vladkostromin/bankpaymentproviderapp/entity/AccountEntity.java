package com.vladkostromin.bankpaymentproviderapp.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

@Table("accounts")
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class AccountEntity extends BaseEntity {


    private Integer amount;
    private Long userId;

    @Transient
    private List<CreditCardEntity> creditCards;
}
