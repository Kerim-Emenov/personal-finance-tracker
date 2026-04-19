package com.example.personal_finance_tracker.service;

import com.example.personal_finance_tracker.dto.request.TransactionRequest;
import com.example.personal_finance_tracker.dto.response.MonthlySummaryResponse;
import com.example.personal_finance_tracker.dto.response.TransactionResponse;
import com.example.personal_finance_tracker.entity.*;
import com.example.personal_finance_tracker.exception.ResourceNotFoundException;
import com.example.personal_finance_tracker.repository.AccountRepository;
import com.example.personal_finance_tracker.repository.CategoryRepository;
import com.example.personal_finance_tracker.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final AccountService accountService;

    @Transactional
    public TransactionResponse create(TransactionRequest request) {
        User user = accountService.getCurrentUser();

        Account account = accountRepository.findByIdAndUserId(request.getAccountId(), user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (request.getType() == TransactionType.EXPENSE) {
            account.setBalance(account.getBalance() - request.getAmount());
        } else {
            account.setBalance(account.getBalance() + request.getAmount());
        }
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setDate(request.getDate());
        transaction.setType(request.getType());
        transaction.setAccount(account);
        transaction.setCategory(category);
        transaction.setUser(user);

        return toResponse(transactionRepository.save(transaction));
    }

    public List<TransactionResponse> getAll() {
        return transactionRepository.findByUserId(accountService.getCurrentUser().getId())
                .stream().map(this::toResponse).toList();
    }

    public List<TransactionResponse> getByType(TransactionType type) {
        return transactionRepository.findByUserIdAndType(accountService.getCurrentUser().getId(), type)
                .stream().map(this::toResponse).toList();
    }

    public MonthlySummaryResponse getMonthlySummary(int year, int month) {
        Long userId = accountService.getCurrentUser().getId();
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        Double income = transactionRepository.sumByUserIdAndTypeAndDateBetween(userId, TransactionType.INCOME, start, end);
        Double expense = transactionRepository.sumByUserIdAndTypeAndDateBetween(userId, TransactionType.EXPENSE, start, end);

        MonthlySummaryResponse summary = new MonthlySummaryResponse();
        summary.setYear(year);
        summary.setMonth(month);
        summary.setTotalIncome(income);
        summary.setTotalExpense(expense);
        summary.setNetSavings(income - expense);
        return summary;
    }

    private TransactionResponse toResponse(Transaction t) {
        TransactionResponse response = new TransactionResponse();
        response.setId(t.getId());
        response.setAmount(t.getAmount());
        response.setDescription(t.getDescription());
        response.setDate(t.getDate());
        response.setType(t.getType());
        response.setAccountName(t.getAccount().getName());
        response.setCategoryName(t.getCategory().getName());
        return response;
    }
}