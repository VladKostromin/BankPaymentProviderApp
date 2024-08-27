package com.vladkostromin.bankpaymentproviderapp.enums;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PaymentMethod {
    CREDIT_CARD("CREDIT_CARD"), CASH("CASH"), BANK_TRANSFER("BANK_TRANSFER"), MOBILE_PAYMENT("MOBILE_PAYMENT"), DIGITAL_PAYMENT("DIGITAL_PAYMENT");

    private final String code;

    PaymentMethod(String code) {
        this.code = code;
    }

    @JsonCreator
    public static PaymentMethod fromCode(String code) {
        for (PaymentMethod paymentMethod : PaymentMethod.values()) {
            if (paymentMethod.code.equals(code)) {
                return paymentMethod;
            }
        }
        throw new IllegalArgumentException("No PaymentMethod found with code " + code);
    }
    @JsonValue
    public String getCode() {
        return code;
    }
    @Override
    public String toString() {
        return code;
    }
}
