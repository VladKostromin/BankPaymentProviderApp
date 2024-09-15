package com.vladkostromin.bankpaymentproviderapp.entity;

import com.vladkostromin.bankpaymentproviderapp.enums.Country;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

@Table("customers")
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class CustomerEntity extends BaseEntity {

    private String firstName;
    private String lastName;
    private Country country;
    private Long userId;

    @Transient
    private AccountEntity account;
    @Transient
    private UserEntity user;
}
