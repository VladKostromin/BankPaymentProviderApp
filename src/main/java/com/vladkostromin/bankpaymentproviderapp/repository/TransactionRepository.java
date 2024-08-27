package com.vladkostromin.bankpaymentproviderapp.repository;


import com.vladkostromin.bankpaymentproviderapp.entity.TransactionEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import java.util.UUID;

public interface TransactionRepository extends R2dbcRepository<TransactionEntity, UUID> {
}
