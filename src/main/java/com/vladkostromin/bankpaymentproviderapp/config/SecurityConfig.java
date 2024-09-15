package com.vladkostromin.bankpaymentproviderapp.config;

import com.vladkostromin.bankpaymentproviderapp.security.AuthenticationManager;
import com.vladkostromin.bankpaymentproviderapp.security.BasicServerAuthenticationConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, AuthenticationManager authenticationManager, BasicServerAuthenticationConverter basicServerAuthenticationConverter) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec
                        .pathMatchers(HttpMethod.POST, "/api/v1/merchants/registration")
                        .permitAll()
                        .anyExchange()
                        .authenticated())
                .authenticationManager(authenticationManager)
                .addFilterAt(authenticationWebFilter(authenticationManager, basicServerAuthenticationConverter), SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationWebFilter authenticationWebFilter(AuthenticationManager authenticationManager, BasicServerAuthenticationConverter basicServerAuthenticationConverter) {
        AuthenticationWebFilter basicAuthenticationFilter = new AuthenticationWebFilter(authenticationManager);
        basicAuthenticationFilter.setServerAuthenticationConverter(basicServerAuthenticationConverter);
        basicAuthenticationFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/**"));
        return basicAuthenticationFilter;
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8090/")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultUriVariables(Collections.singletonMap("url", "http://localhost:8090/"))
                .build();

    }
}
