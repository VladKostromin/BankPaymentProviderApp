package com.vladkostromin.bankpaymentproviderapp.utils;

import com.vladkostromin.bankpaymentproviderapp.entity.AccountEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class AccountDataUtils {

    public static AccountEntity getAccountTransient() {
        return AccountEntity.builder()
                .amount(5000)
                .creditCards(new ArrayList<>())
                .userId(1L)
                .build();
    }


    public static AccountEntity getAccountPersistent() {
        return AccountEntity.builder()
                .id(1L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .amount(5000)
                .creditCards(new ArrayList<>())
                .userId(1L)
                .build();
    }



}
