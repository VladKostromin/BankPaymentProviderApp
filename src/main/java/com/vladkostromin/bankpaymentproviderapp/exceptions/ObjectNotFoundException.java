package com.vladkostromin.bankpaymentproviderapp.exceptions;


public class ObjectNotFoundException extends ApiException {
    public ObjectNotFoundException(String message) {
        super(message, "OBJECT_NOT_FOUND");
    }
}
