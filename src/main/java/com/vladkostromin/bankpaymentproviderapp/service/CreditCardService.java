package com.vladkostromin.bankpaymentproviderapp.service;

import com.vladkostromin.bankpaymentproviderapp.entity.AccountEntity;
import com.vladkostromin.bankpaymentproviderapp.entity.CreditCardEntity;
import com.vladkostromin.bankpaymentproviderapp.exceptions.ObjectNotFoundException;
import com.vladkostromin.bankpaymentproviderapp.repository.CreditCardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreditCardService {
    private final CreditCardRepository creditCardRepository;


    public Mono<CreditCardEntity> createCreditCard(CreditCardEntity creditCard, AccountEntity account) {
        log.info("IN createCreditCard");
        return creditCardRepository.save(CreditCardEntity.builder()
                        .cardNumber(creditCard.getCardNumber())
                        .expDate(creditCard.getExpDate())
                        .cvv(creditCard.getCvv())
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .accountId(account.getId())
                .build());
    }

    public Mono<CreditCardEntity> getCreditCardById(Long cardId) {
        log.info("Getting credit card by id: {}", cardId);
        return creditCardRepository.findById(cardId)
                .switchIfEmpty(Mono.error(new ObjectNotFoundException("Credit card not found")));
    }

    public Mono<CreditCardEntity> updateCreditCard(CreditCardEntity creditCard) {
        log.info("IN updateCreditCard");
        creditCard.setUpdatedAt(LocalDateTime.now());
        return creditCardRepository.findById(creditCard.getId())
                .switchIfEmpty(Mono.error(new ObjectNotFoundException("Credit card not found")))
                .flatMap(existingCreditCard -> creditCardRepository.save(creditCard));
    }

    public Mono<CreditCardEntity> getCreditCardByCardNumber(String cardNumber) {
        log.info("IN getCreditCardByCardNumber");
        return creditCardRepository.findByCardNumber(cardNumber)
                .switchIfEmpty(Mono.error(new ObjectNotFoundException("Credit card not found")));
    }
    public Flux<CreditCardEntity> getAllCreditCardsByAccountId(Long accountId) {
        log.info("IN getAllCreditCardsByAccountId");
        return creditCardRepository.findAllByAccountId(accountId)
                .collectList()
                .flatMapMany(list -> {
                    if(list.isEmpty()) {
                        return Mono.error(new ObjectNotFoundException("Credit cards not found"));
                    }
                    return Flux.fromIterable(list);
                });

    }

}
