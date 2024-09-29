package com.vladkostromin.bankpaymentproviderapp.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum WebHookStatus {
    SENT("SENT"), FAILED("FAILED");

    private final String status;

    WebHookStatus(String code) {
        this.status = code;
    }

    @JsonCreator
    public static WebHookStatus fromCode(String code) {
        for (WebHookStatus webHookStatus : WebHookStatus.values()) {
            if (webHookStatus.status.equals(code)) {
                return webHookStatus;
            }
        }
        throw new IllegalArgumentException("No status with this code: " + code);
    }
    @JsonValue
    public String getStatus() {
        return this.status;
    }
    @Override
    public String toString() {
        return this.status;
    }
}
