package com.vladkostromin.bankpaymentproviderapp.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Table("accounts")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class AccountEntity {

    @Id
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer amount;
    private Long userId;

    @Transient
    private Set<CreditCardEntity> creditCards = new HashSet<>();
}
