package com.vladkostromin.bankpaymentproviderapp.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("merchants")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class MerchantEntity {

    @Id
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String merchantName;
    private String password;
    private Long userId;

    @Transient
    private UserEntity user;
}
