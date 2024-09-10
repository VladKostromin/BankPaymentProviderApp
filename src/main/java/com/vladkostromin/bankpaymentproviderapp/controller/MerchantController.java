package com.vladkostromin.bankpaymentproviderapp.controller;

import com.vladkostromin.bankpaymentproviderapp.dto.MerchantDto;
import com.vladkostromin.bankpaymentproviderapp.entity.MerchantEntity;
import com.vladkostromin.bankpaymentproviderapp.mapper.MerchantMapper;
import com.vladkostromin.bankpaymentproviderapp.service.MerchantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/merchants")
@RequiredArgsConstructor
@Slf4j
public class MerchantController {

    private final MerchantMapper merchantMapper;
    private final MerchantService merchantService;

    @PostMapping("/registration")
    public Mono<MerchantDto> createMerchant(@RequestBody MerchantDto merchantDto) {
        log.info("Creating merchant: {}", merchantDto);
        MerchantEntity merchantEntity = merchantMapper.map(merchantDto);
        return merchantService.saveMerchant(merchantEntity).map(merchantMapper::map);

    }
}
