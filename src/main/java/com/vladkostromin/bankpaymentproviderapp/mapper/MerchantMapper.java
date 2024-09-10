package com.vladkostromin.bankpaymentproviderapp.mapper;

import com.vladkostromin.bankpaymentproviderapp.dto.MerchantDto;
import com.vladkostromin.bankpaymentproviderapp.entity.MerchantEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MerchantMapper {
    MerchantDto map(MerchantEntity merchantEntity);
    @InheritInverseConfiguration
    MerchantEntity map(MerchantDto merchantDto);


}
