package com.vladkostromin.bankpaymentproviderapp.exceptions;

public class NotEnoughCurrencyException extends ApiException {

    public NotEnoughCurrencyException(String message) {
        super(message, "ERROR_CODE_NOT_ENOUGH_CURRENCY");
    }
}
