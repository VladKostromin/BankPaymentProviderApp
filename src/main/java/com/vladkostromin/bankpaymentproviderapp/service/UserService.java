package com.vladkostromin.bankpaymentproviderapp.service;

import com.vladkostromin.bankpaymentproviderapp.entity.UserEntity;
import com.vladkostromin.bankpaymentproviderapp.enums.UserStatus;
import com.vladkostromin.bankpaymentproviderapp.exceptions.ObjectNotFoundException;
import com.vladkostromin.bankpaymentproviderapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    public Mono<UserEntity> createUser() {
        return userRepository.save(UserEntity.builder()
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .status(UserStatus.ACTIVE)
                .build());
    }

    public Mono<UserEntity> getUserById(Long id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new ObjectNotFoundException("User not found")));
    }

    public Mono<UserEntity> banUser(UserEntity userEntity) {
        userEntity.setStatus(UserStatus.BANNED);
        return userRepository.save(userEntity);
    }

    public Mono<UserEntity> safeDeleteUser(UserEntity userEntity) {
        userEntity.setStatus(UserStatus.BANNED);
        return userRepository.save(userEntity);
    }
    public Mono<UserEntity> restoreBannedDeletedUser(UserEntity userEntity) {
        userEntity.setStatus(UserStatus.ACTIVE);
        return userRepository.save(userEntity);
    }
}
