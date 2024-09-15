package com.vladkostromin.bankpaymentproviderapp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class BaseEntity {

    @Id
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
