package com.vladkostromin.bankpaymentproviderapp.exceptions;

public class PaymentMethodNotAllowedException extends ApiException {

    public PaymentMethodNotAllowedException(String message) {
        super(message, "ERROR_CODE_PAYMENT_METHOD_NOT_ALLOWED");
    }
}
