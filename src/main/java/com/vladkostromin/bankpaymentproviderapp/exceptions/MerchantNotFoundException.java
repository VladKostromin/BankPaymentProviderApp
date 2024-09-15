package com.vladkostromin.bankpaymentproviderapp.exceptions;

public class MerchantNotFoundException extends ApiException {

    public MerchantNotFoundException(String message) {
        super(message, "ERROR_CODE_MERCHANT_NOT_FOUND");
    }
}
