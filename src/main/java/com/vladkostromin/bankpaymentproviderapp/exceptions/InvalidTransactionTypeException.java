package com.vladkostromin.bankpaymentproviderapp.exceptions;

public class InvalidTransactionTypeException extends ApiException {

    public InvalidTransactionTypeException(String message) {
        super(message, "ERROR_CODE_INVALID_TRANSACTION_TYPE");
    }
}
