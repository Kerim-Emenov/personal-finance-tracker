package com.example.personal_finance_tracker.service;

import com.example.personal_finance_tracker.dto.request.AccountRequest;
import com.example.personal_finance_tracker.dto.response.AccountResponse;
import com.example.personal_finance_tracker.entity.Account;
import com.example.personal_finance_tracker.entity.User;
import com.example.personal_finance_tracker.exception.ResourceNotFoundException;
import com.example.personal_finance_tracker.repository.AccountRepository;
import com.example.personal_finance_tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountResponse create(AccountRequest request) {
        User user = getCurrentUser();
        Account account = new Account();
        account.setName(request.getName());
        account.setBalance(request.getInitialBalance());
        account.setType(request.getType());
        account.setUser(user);
        return toResponse(accountRepository.save(account));
    }

    public List<AccountResponse> getAll() {
        return accountRepository.findByUserId(getCurrentUser().getId())
                .stream().map(this::toResponse).toList();
    }

    public AccountResponse getById(Long id) {
        User user = getCurrentUser();
        return toResponse(accountRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + id)));
    }

    public void delete(Long id) {
        User user = getCurrentUser();
        Account account = accountRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + id));
        accountRepository.delete(account);
    }

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private AccountResponse toResponse(Account account) {
        AccountResponse response = new AccountResponse();
        response.setId(account.getId());
        response.setName(account.getName());
        response.setBalance(account.getBalance());
        response.setType(account.getType());
        response.setCreatedAt(account.getCreatedAt());
        return response;
    }
}