package com.vladkostromin.bankpaymentproviderapp.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.vladkostromin.bankpaymentproviderapp.dto.MerchantDto;
import com.vladkostromin.bankpaymentproviderapp.entity.MerchantEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.PropertyNamingStrategy;

import java.time.LocalDateTime;

public class MerchantDataUtils {
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static MerchantEntity getMerchantDataTransient() {
        return MerchantEntity.builder()
                .merchantName("TestMerchantName")
                .password("TestMerchantPassword")
                .build();
    }
    public static MerchantEntity getMerchantDataPersisted() {
        return MerchantEntity.builder()
                .id(1L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .userId(UserDataUtils.getUserData().getId())
                .user(UserDataUtils.getUserData())
                .merchantName("TestMerchantName")
                .password(passwordEncoder.encode("TestMerchantPassword"))
                .build();
    }

    public static MerchantDto getMerchantDataDtoTransient() {
        return MerchantDto.builder()
                .merchantName("TestMerchantName")
                .password("TestMerchantPassword")
                .build();
    }
}
