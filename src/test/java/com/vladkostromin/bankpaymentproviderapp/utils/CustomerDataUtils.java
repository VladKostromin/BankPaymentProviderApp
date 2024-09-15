package com.vladkostromin.bankpaymentproviderapp.utils;

import com.vladkostromin.bankpaymentproviderapp.entity.CustomerEntity;
import com.vladkostromin.bankpaymentproviderapp.enums.Country;

import java.time.LocalDateTime;

public class CustomerDataUtils {

    public static CustomerEntity getCustomerTransient() {
        return CustomerEntity.builder()
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .firstName("firstName")
                .lastName("lastName")
                .country(Country.IS)
                .build();
    }

    public static CustomerEntity getCustomerPersistent() {
        return CustomerEntity.builder()
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .id(1L)
                .firstName("firstName")
                .lastName("lastName")
                .country(Country.IS)
                .userId(1L)
                .build();
    }
}
