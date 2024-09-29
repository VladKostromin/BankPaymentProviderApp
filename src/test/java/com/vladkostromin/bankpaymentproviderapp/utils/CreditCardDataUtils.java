package com.vladkostromin.bankpaymentproviderapp.utils;

import com.vladkostromin.bankpaymentproviderapp.dto.CreditCardDto;
import com.vladkostromin.bankpaymentproviderapp.entity.CreditCardEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

public class CreditCardDataUtils {

    private final static Random random = new Random();

    public static CreditCardEntity getCreditCardDataTransient() {
        return CreditCardEntity.builder()
                .cardNumber("4123456789123456")
                .expDate("11/26")
                .cvv("567")
                .build();
    }

    public static CreditCardEntity getCreditCardDataPersistent() {
        return CreditCardEntity.builder()
                .id(1L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .cardNumber("4123456789123456")
                .expDate("11/26")
                .cvv("567")
                .accountId(1L)
                .build();
    }

    public static List<CreditCardEntity> getCreditCardDataList() {
        return List.of(getCreditCardDataPersistent(), getCreditCardDataPersistent());
    }

    public static CreditCardDto getCreditCardDataDtoTransient() {
        return CreditCardDto.builder()
                .cardNumber("4123456789123456")
                .expDate("11/26")
                .cvv("567")
                .build();
    }
}
