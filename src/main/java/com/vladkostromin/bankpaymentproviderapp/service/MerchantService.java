package com.vladkostromin.bankpaymentproviderapp.service;

import com.vladkostromin.bankpaymentproviderapp.entity.MerchantEntity;
import com.vladkostromin.bankpaymentproviderapp.exceptions.ObjectNotFoundException;
import com.vladkostromin.bankpaymentproviderapp.repository.MerchantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MerchantService {

    private final MerchantRepository merchantRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountService accountService;
    private final UserService userService;


    public Mono<MerchantEntity> saveMerchant(MerchantEntity merchantEntity) {
        return userService.createUser()
                .flatMap(user -> {
                    merchantEntity.setUserId(user.getId());
                    return accountService.createAccount(user.getId());
                })
                .flatMap(account -> {
                    merchantEntity.setCreatedAt(LocalDateTime.now());
                    merchantEntity.setUpdatedAt(LocalDateTime.now());
                    merchantEntity.setPassword(passwordEncoder.encode(merchantEntity.getPassword()));
                    return merchantRepository.save(merchantEntity);
                });
    }

    public Mono<MerchantEntity> getMerchantByMerchantName(String merchantName) {
        return merchantRepository.findByMerchantName(merchantName)
                .switchIfEmpty(Mono.error(new ObjectNotFoundException("Merchant with name " + merchantName + " not found")))
                .flatMap(merchant -> userService.getUserById(merchant.getUserId())
                        .flatMap(user -> {
                            merchant.setUser(user);
                            return Mono.just(merchant);
                        }));
    }

    public Mono<Void> deleteAllMerchants() {
        return merchantRepository.deleteAll();
    }
}
