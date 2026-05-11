package com.moneymanagerproject.service;

import com.moneymanagerproject.dto.ExpenseDto;
import com.moneymanagerproject.dto.IncomeDto;
import com.moneymanagerproject.dto.RecentTransactionDto;
import com.moneymanagerproject.entity.ProfileEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Stream.concat;

@Service
@RequiredArgsConstructor
public class DashBoardService {

    private final IncomeService incomeService;
    private final ExpenseService expenseService;
    private final ProfileService profileService;

    public Map<String, Object> getDashBoardData() {
        ProfileEntity profile = profileService.getCurrentProfile();
        Map<String, Object> returnValue = new LinkedHashMap<>();

        List<IncomeDto> latestIncome = incomeService.get5Income();
        List<ExpenseDto> latestExpense = expenseService.get5Expense();

        List<RecentTransactionDto> recentTransactions = concat(
                latestIncome.stream().map(income -> RecentTransactionDto.builder()
                        .id(income.getId())
                        .profileId(profile.getId())
                        .icon(income.getIcon())
                        .name(income.getName())
                        .amount(income.getAmount())
                        .date(income.getDate())
                        .createdAt(income.getCreatedAt())
                        .updatedAt(income.getUpdatedAt())
                        .type("income")
                        .build()
                ),
                latestExpense.stream().map(expense -> RecentTransactionDto.builder()
                        .id(expense.getId())
                        .profileId(profile.getId())
                        .icon(expense.getIcon())
                        .name(expense.getName())
                        .amount(expense.getAmount())
                        .date(expense.getDate())
                        .createdAt(expense.getCreatedAt())
                        .updatedAt(expense.getUpdatedAt())
                        .type("expense")
                        .build()
                )
        ).sorted((a, b) -> {
            int cmp = b.getDate().compareTo(a.getDate());
            if (cmp == 0 && a.getCreatedAt() != null && b.getCreatedAt() != null) {
                return b.getCreatedAt().compareTo(a.getCreatedAt());
            }
            return cmp;
        }).collect(Collectors.toList());

        returnValue.put("totalBalance", incomeService.totalIncomeOfCurrentUser()
                .subtract(expenseService.totalExpenseOfCurrentUser()));
        returnValue.put("TotalIncome", incomeService.totalIncomeOfCurrentUser());
        returnValue.put("TotalExpense", expenseService.totalExpenseOfCurrentUser());
        returnValue.put("Recent5Expense", expenseService.get5Expense());
        returnValue.put("Recent5Income", incomeService.get5Income());
        returnValue.put("RecentTransactions", recentTransactions);
        return returnValue;
    }
}