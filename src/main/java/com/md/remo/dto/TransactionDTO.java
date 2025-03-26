package com.md.remo.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class TransactionDTO {
    private String userId;
    private BigDecimal amount;
    private LocalDateTime timestamp;
    private String transactionType;
}