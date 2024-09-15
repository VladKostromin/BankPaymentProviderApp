package com.vladkostromin.bankpaymentproviderapp.exceptions;

public class AccountNotFoundException extends ApiException {
    public AccountNotFoundException(String message) {
        super(message, "ERROR_CODE_ACCOUNT_NOT_FOUND");
    }
}
