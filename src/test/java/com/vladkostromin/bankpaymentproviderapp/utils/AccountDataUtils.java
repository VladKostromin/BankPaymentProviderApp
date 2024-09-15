package com.vladkostromin.bankpaymentproviderapp.utils;

import com.vladkostromin.bankpaymentproviderapp.entity.AccountEntity;
import com.vladkostromin.bankpaymentproviderapp.entity.CreditCardEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AccountDataUtils {

    public static AccountEntity getAccountTransient() {
        return AccountEntity.builder()
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
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
