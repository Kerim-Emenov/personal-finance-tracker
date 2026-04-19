package com.example.personal_finance_tracker.dto.response;

import com.example.personal_finance_tracker.entity.AccountType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AccountResponse {
    private Long id;
    private String name;
    private Double balance;
    private AccountType type;
    private LocalDateTime createdAt;
}