package com.vladkostromin.bankpaymentproviderapp.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreditCardDto extends BaseDto {


    private String cardNumber;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String expDate;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String cvv;
}
