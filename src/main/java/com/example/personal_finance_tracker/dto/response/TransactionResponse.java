package com.example.personal_finance_tracker.dto.response;

import com.example.personal_finance_tracker.entity.TransactionType;
import lombok.Data;
import java.time.LocalDate;

@Data
public class TransactionResponse {
    private Long id;
    private Double amount;
    private String description;
    private LocalDate date;
    private TransactionType type;
    private String accountName;
    private String categoryName;
}