package com.vladkostromin.bankpaymentproviderapp.enums;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PaymentMethod {
    CREDIT_CARD("CREDIT_CARD"), CASH("CASH"), BANK_TRANSFER("BANK_TRANSFER"), MOBILE_PAYMENT("MOBILE_PAYMENT"), DIGITAL_PAYMENT("DIGITAL_PAYMENT");

    private final String method;

    PaymentMethod(String code) {
        this.method = code;
    }

    @JsonCreator
    public static PaymentMethod fromCode(String code) {
        for (PaymentMethod paymentMethod : PaymentMethod.values()) {
            if (paymentMethod.method.equals(code)) {
                return paymentMethod;
            }
        }
        throw new IllegalArgumentException("No PaymentMethod found with code " + code);
    }
    @JsonValue
    public String getMethod() {
        return this.method;
    }
    @Override
    public String toString() {
        return this.method;
    }
}
