package com.vladkostromin.bankpaymentproviderapp.exceptions;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {

    private final String errorCode;

    public ApiException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }


}
