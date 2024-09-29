package com.vladkostromin.bankpaymentproviderapp.service;

import com.vladkostromin.bankpaymentproviderapp.entity.MerchantEntity;
import com.vladkostromin.bankpaymentproviderapp.exceptions.ObjectNotFoundException;
import com.vladkostromin.bankpaymentproviderapp.repository.MerchantRepository;
import com.vladkostromin.bankpaymentproviderapp.utils.AccountDataUtils;
import com.vladkostromin.bankpaymentproviderapp.utils.MerchantDataUtils;
import com.vladkostromin.bankpaymentproviderapp.utils.UserDataUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MerchantServiceTests {

    @Mock
    private MerchantRepository merchantRepository;

    @Spy
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Mock
    private AccountService accountService;

    @Mock
    private UserService userService;

    @InjectMocks
    private MerchantService merchantServiceUnderTest;

    private AutoCloseable autoCloseable;

    @BeforeEach
    public void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);

        merchantServiceUnderTest = new MerchantService(merchantRepository, passwordEncoder, accountService, userService);
    }

    @AfterEach
    public void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    @DisplayName("Test create merchant functionality")
    public void giveMerchantEntity_whenCreateMerchant_thenMerchantIsSavedAccountAndUserIsCreated() {
        //given
        MerchantEntity merchantToSave = MerchantDataUtils.getMerchantDataTransient();
        String rawPassword = merchantToSave.getPassword();
        BDDMockito.given(userService.createUser())
                .willReturn(Mono.just(UserDataUtils.getUserData()));
        BDDMockito.given(accountService.createAccount(anyLong()))
                .willReturn(Mono.just(AccountDataUtils.getAccountPersistent()));
        BDDMockito.given(merchantRepository.save(any(MerchantEntity.class)))
                .willReturn(Mono.just(MerchantDataUtils.getMerchantDataPersisted()));
        //when
        Mono<MerchantEntity> savedMerchant = merchantServiceUnderTest.saveMerchant(merchantToSave);
        //then
        StepVerifier.create(savedMerchant)
                .assertNext(merchant -> {
                    assertThat(merchant).isNotNull();
                    assertThat(merchant.getMerchantName()).isEqualTo(merchantToSave.getMerchantName());
                    assertThat(passwordEncoder.matches(rawPassword, merchant.getPassword())).isTrue();
                })
                .verifyComplete();
        verify(userService,times(1)).createUser();
        verify(accountService,times(1)).createAccount(anyLong());
        verify(passwordEncoder,times(1)).encode(anyString());
        verify(merchantRepository,times(1)).save(any(MerchantEntity.class));
    }
    @Test
    @DisplayName("Test get merchant by merchant name functionality")
    public void given_MerchantName_whenGetMerchantByMerchantName_thenMerchantIsReturned() {
        //given
        String merchantName = MerchantDataUtils.getMerchantDataTransient().getMerchantName();
        BDDMockito.given(merchantRepository.findByMerchantName(anyString()))
                .willReturn(Mono.just(MerchantDataUtils.getMerchantDataPersisted()));
        BDDMockito.given(userService.getUserById(anyLong()))
                .willReturn(Mono.just(UserDataUtils.getUserData()));
        //when
        Mono<MerchantEntity> obtainedMerchant = merchantServiceUnderTest.getMerchantByMerchantName(merchantName);
        //then
        StepVerifier.create(obtainedMerchant)
                .assertNext(merchant -> {
                    assertThat(merchant).isNotNull();
                    assertThat(merchant.getUser()).isNotNull();
                    assertThat(merchant.getMerchantName()).isEqualTo(merchantName);
                })
                .verifyComplete();
        verify(merchantRepository,times(1)).findByMerchantName(anyString());
        verify(userService,times(1)).getUserById(anyLong());
    }

    @Test
    @DisplayName("Test get merchant by merchant name functionality")
    public void given_MerchantName_whenGetMerchantByMerchantName_thenExceptionIsThrown() {
        //given
        String merchantName = MerchantDataUtils.getMerchantDataTransient().getMerchantName();
        BDDMockito.given(merchantRepository.findByMerchantName(anyString()))
                .willReturn(Mono.empty());
        //when
        Mono<MerchantEntity> obtainedMerchant = merchantServiceUnderTest.getMerchantByMerchantName(merchantName);
        //then
        StepVerifier.create(obtainedMerchant)
                .expectErrorMatches(throwable -> throwable instanceof ObjectNotFoundException &&
                        throwable.getMessage().equals("Merchant with name " + merchantName + " not found"))
                .verify();
        verify(merchantRepository,times(1)).findByMerchantName(anyString());
        verify(userService, never()).getUserById(anyLong());
    }
}
