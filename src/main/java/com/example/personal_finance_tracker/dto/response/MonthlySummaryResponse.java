package com.example.personal_finance_tracker.dto.response;

import lombok.Data;

@Data
public class MonthlySummaryResponse {
    private int year;
    private int month;
    private Double totalIncome;
    private Double totalExpense;
    private Double netSavings;
}