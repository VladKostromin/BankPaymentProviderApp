package com.vladkostromin.bankpaymentproviderapp.repository;

import com.vladkostromin.bankpaymentproviderapp.entity.WebhookEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface WebHookRepository extends R2dbcRepository<WebhookEntity, Long> {
}
