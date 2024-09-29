package com.vladkostromin.bankpaymentproviderapp.utils;

import com.vladkostromin.bankpaymentproviderapp.entity.UserEntity;
import com.vladkostromin.bankpaymentproviderapp.enums.UserStatus;

import java.time.LocalDateTime;

public class UserDataUtils {


    public static UserEntity getUserData() {
        return UserEntity.builder()
                .id(1L)
                .status(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
