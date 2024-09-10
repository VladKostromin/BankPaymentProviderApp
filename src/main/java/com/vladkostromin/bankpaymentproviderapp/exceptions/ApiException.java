package com.vladkostromin.bankpaymentproviderapp.exceptions;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {

    private final String message;

    public ApiException(String message, String errorCode) {
        super(message);
        this.message = errorCode;
    }
}
