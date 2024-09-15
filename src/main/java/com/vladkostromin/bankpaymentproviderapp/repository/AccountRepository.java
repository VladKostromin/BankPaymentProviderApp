package com.vladkostromin.bankpaymentproviderapp.repository;

import com.vladkostromin.bankpaymentproviderapp.entity.AccountEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface AccountRepository extends R2dbcRepository<AccountEntity, Long> {
    Mono<AccountEntity> findByUserId(Long userId);
}
