package com.vladkostromin.bankpaymentproviderapp.security;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class BasicServerAuthenticationConverter implements ServerAuthenticationConverter {

    private final BasicAuthHandler basicAuthHandler;

    private static final String BASIC = "Basic ";
    private static final Function<String, Mono<String>> basicCredentialsValue = value -> Mono.justOrEmpty(value.substring(BASIC.length()));
    private static final Function<ServerWebExchange, Mono<String>> header = exchange -> Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION));
    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return Mono.justOrEmpty(exchange)
                .flatMap(header)
                .flatMap(basicCredentialsValue)
                .flatMap(basicAuthHandler::createAuthentication);
    }
}
