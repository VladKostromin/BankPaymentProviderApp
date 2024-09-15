package com.vladkostromin.bankpaymentproviderapp.security;

import com.vladkostromin.bankpaymentproviderapp.exceptions.MerchantNotFoundException;
import com.vladkostromin.bankpaymentproviderapp.exceptions.ObjectNotFoundException;
import com.vladkostromin.bankpaymentproviderapp.exceptions.UnauthorizedException;
import com.vladkostromin.bankpaymentproviderapp.repository.MerchantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final MerchantRepository merchantRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String merchantName = authentication.getPrincipal().toString();
        String password = authentication.getCredentials().toString();
        return merchantRepository.findByMerchantName(merchantName)
                .switchIfEmpty(Mono.error(new ObjectNotFoundException("Merchant not found")))
                .flatMap(merchant -> {
                    if(passwordEncoder.matches(password, merchant.getPassword())) {
                        return Mono.just(authentication);
                    } else {
                        return Mono.error(new UnauthorizedException("Invalid credentials"));
                    }
                });
    }
}
