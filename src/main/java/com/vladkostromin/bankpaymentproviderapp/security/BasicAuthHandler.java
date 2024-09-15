package com.vladkostromin.bankpaymentproviderapp.security;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.Collections;
import java.util.List;

@Component
public class BasicAuthHandler {

    public Mono<Authentication> createAuthenticationToken(String rawCredentials) {
        String credentials = new String(Base64.getDecoder().decode(rawCredentials));
        String[] parts = credentials.split(":", 2);
        if(parts.length == 2) {
            String merchantId = parts[0];
            String merchantSecretKey = parts[1];
            List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_MERCHANT"));
            return Mono.just(new UsernamePasswordAuthenticationToken(merchantId, merchantSecretKey, authorities));
        } else {
            return Mono.error(new BadCredentialsException("Invalid credentials"));
        }
    }

}
