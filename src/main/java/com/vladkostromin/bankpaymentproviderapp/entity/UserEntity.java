package com.vladkostromin.bankpaymentproviderapp.entity;

import com.vladkostromin.bankpaymentproviderapp.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Table;

@Table("users")
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class UserEntity extends BaseEntity {

    
    private UserStatus status;
}
