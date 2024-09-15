package com.vladkostromin.bankpaymentproviderapp.exceptions.errorhandler;

import com.vladkostromin.bankpaymentproviderapp.exceptions.*;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.*;

@Component
public class AppErrorAttributes extends DefaultErrorAttributes {

    private HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

    public AppErrorAttributes() {
        super();
    }

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Map<String, Object> errorAttributes = super.getErrorAttributes(request, ErrorAttributeOptions.defaults());
        Throwable error = getError(request);

        List<Map<String, Object>> errorList = new ArrayList<>();

        if (error instanceof UnauthorizedException) {
            status = HttpStatus.UNAUTHORIZED;
            LinkedHashMap<String, Object> errorDetails = new LinkedHashMap<>();
            errorDetails.put("code", ((ApiException) error).getErrorCode());
            errorDetails.put("message", error.getMessage());
            errorList.add(errorDetails);
        } else if(error instanceof MerchantNotFoundException) {
            status = HttpStatus.NOT_FOUND;
            LinkedHashMap<String, Object> errorDetails = new LinkedHashMap<>();
            errorDetails.put("code", ((ApiException) error).getErrorCode());
            errorDetails.put("message", error.getMessage());
            errorList.add(errorDetails);
        } else if(error instanceof ObjectNotFoundException) {
            status = HttpStatus.NOT_FOUND;
            LinkedHashMap<String, Object> errorDetails = new LinkedHashMap<>();
            errorDetails.put("code", ((ApiException) error).getErrorCode());
            errorDetails.put("message", error.getMessage());
            errorList.add(errorDetails);
        } else if (error instanceof NotEnoughCurrencyException) {
            status = HttpStatus.BAD_REQUEST;
            LinkedHashMap<String, Object> errorDetails = new LinkedHashMap<>();
            errorDetails.put("code", ((ApiException) error).getErrorCode());
            errorDetails.put("message", error.getMessage());
            errorList.add(errorDetails);
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            String message = error.getMessage();
            if(message == null) {
                message = error.getClass().getName();
            }
            LinkedHashMap<String, Object> errorDetails = new LinkedHashMap<>();
            errorDetails.put("code", "INTERNAL_ERROR");
            errorDetails.put("message", message);
            errorList.add(errorDetails);
        }
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("errors", errorList);
        errorAttributes.put("status", status.value());
        errorAttributes.put("errors", errorDetails);

        return errorAttributes;
    }
}
