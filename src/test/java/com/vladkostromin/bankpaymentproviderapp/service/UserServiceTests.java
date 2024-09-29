package com.vladkostromin.bankpaymentproviderapp.service;


import com.vladkostromin.bankpaymentproviderapp.entity.UserEntity;
import com.vladkostromin.bankpaymentproviderapp.enums.UserStatus;
import com.vladkostromin.bankpaymentproviderapp.exceptions.ObjectNotFoundException;
import com.vladkostromin.bankpaymentproviderapp.repository.UserRepository;
import com.vladkostromin.bankpaymentproviderapp.utils.UserDataUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userServiceUnderTest;

    private AutoCloseable autoCloseable;

    @BeforeEach
    public void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);

        userServiceUnderTest = new UserService(userRepository);
    }

    @AfterEach
    public void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    @DisplayName("Test create user functionality")
    public void givenUserCreationRequest_whenCreateUser_thenUserIsCreated() {
        //given
        BDDMockito.given(userRepository.save(any(UserEntity.class)))
                .willReturn(Mono.just(UserDataUtils.getUserData()));
        //when
        Mono<UserEntity> createdUser = userServiceUnderTest.createUser();
        //then
        StepVerifier.create(createdUser)
                .assertNext(userEntity -> {
                    assertThat(userEntity).isNotNull();
                    assertThat(userEntity.getStatus()).isEqualTo(UserStatus.ACTIVE);
                })
                .verifyComplete();
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("Test get user by id functionality")
    public void given_UserId_whenGetUserById_thenUserIsRetrieved() {
        //given
        Long userId = 1L;
        BDDMockito.given(userRepository.findById(anyLong()))
                .willReturn(Mono.just(UserDataUtils.getUserData()));
        //when
        Mono<UserEntity> obtainedUser = userServiceUnderTest.getUserById(userId);
        //then
        StepVerifier.create(obtainedUser)
                .assertNext(userEntity -> {
                    assertThat(userEntity).isNotNull();
                    assertThat(userEntity.getStatus()).isEqualTo(UserStatus.ACTIVE);
                })
                .verifyComplete();
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("Test get user by id functionality")
    public void given_UserId_whenGetUserById_thenExceptionIsThrown() {
        //given
        Long userId = 1L;
        BDDMockito.given(userRepository.findById(anyLong()))
                .willReturn(Mono.empty());
        //when
        Mono<UserEntity> obtainedUser = userServiceUnderTest.getUserById(userId);
        //then
        StepVerifier.create(obtainedUser)
                        .expectErrorMatches(throwable -> throwable instanceof ObjectNotFoundException &&
                                throwable.getMessage().equals("User not found"))
                .verify();
        verify(userRepository, times(1)).findById(anyLong());
    }




}
