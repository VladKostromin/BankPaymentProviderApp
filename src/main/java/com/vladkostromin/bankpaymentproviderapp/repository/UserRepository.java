package com.vladkostromin.bankpaymentproviderapp.repository;

import com.vladkostromin.bankpaymentproviderapp.entity.UserEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface UserRepository extends R2dbcRepository<UserEntity, Long> {
}
