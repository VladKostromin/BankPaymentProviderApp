package com.vladkostromin.bankpaymentproviderapp.repository;

import com.vladkostromin.bankpaymentproviderapp.entity.MerchantEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface MerchantRepository extends R2dbcRepository<MerchantEntity, Long> {
    Mono<MerchantEntity> findByMerchantName(String merchantName);
}
