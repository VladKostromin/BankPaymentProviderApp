package com.vladkostromin.bankpaymentproviderapp.service;

import com.vladkostromin.bankpaymentproviderapp.entity.AccountEntity;
import com.vladkostromin.bankpaymentproviderapp.entity.CreditCardEntity;
import com.vladkostromin.bankpaymentproviderapp.repository.CreditCardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreditCardService {
    private final CreditCardRepository creditCardRepository;

    public Mono<CreditCardEntity> getCreditCardById(Long cardId) {
        log.info("Getting credit card by id: {}", cardId);
        return creditCardRepository.findById(cardId);
    }
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
    public Mono<CreditCardEntity> updateCreditCard(CreditCardEntity creditCard) {
        log.info("IN updateCreditCard");
        creditCard.setUpdatedAt(LocalDateTime.now());
        return creditCardRepository.save(creditCard);
    }

    public Mono<CreditCardEntity> getCreditCardByCardNumber(String cardNumber) {
        log.info("IN getCreditCardByCardNumber");
        return creditCardRepository.findByCardNumber(cardNumber);
    }

    public Mono<CreditCardEntity> addOrFindCreditCard(CreditCardEntity creditCard, AccountEntity account) {
        log.info("IN TransactionService.addOrFindCreditCard");
        return creditCardRepository.findByCardNumber(creditCard.getCardNumber())
                .switchIfEmpty(Mono.defer(() -> createCreditCard(creditCard, account)));
    }
    public Mono<Void> deleteCreditCard(CreditCardEntity creditCard) {
        return creditCardRepository.delete(creditCard);
    }
}
