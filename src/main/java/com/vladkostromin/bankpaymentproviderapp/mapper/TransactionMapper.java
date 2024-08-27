package com.vladkostromin.bankpaymentproviderapp.mapper;


import com.vladkostromin.bankpaymentproviderapp.dto.TransactionDto;
import com.vladkostromin.bankpaymentproviderapp.entity.TransactionEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    TransactionDto map(TransactionEntity transactionEntity);
    @InheritInverseConfiguration
    TransactionEntity map(TransactionDto transactionDto);

}
