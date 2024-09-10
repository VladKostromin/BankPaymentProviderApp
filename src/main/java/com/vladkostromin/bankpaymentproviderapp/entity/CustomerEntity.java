package com.vladkostromin.bankpaymentproviderapp.entity;

import com.vladkostromin.bankpaymentproviderapp.enums.Country;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("customers")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class CustomerEntity {
    @Id
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String firstName;
    private String lastName;
    private Country country;
    private Long userId;

    @Transient
    private AccountEntity account;
    @Transient
    private UserEntity user;
}
