package com.vladkostromin.bankpaymentproviderapp.mapper;

import com.vladkostromin.bankpaymentproviderapp.dto.CreditCardDto;
import com.vladkostromin.bankpaymentproviderapp.entity.CreditCardEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CreditCardMapper {
    CreditCardDto map(CreditCardEntity creditCardEntity);
    @InheritInverseConfiguration
    CreditCardEntity map(CreditCardDto creditCardDto);
}
