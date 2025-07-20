package com.digitalvortex.moneymanager.service;

import com.digitalvortex.moneymanager.dto.ExpenseDTO;
import com.digitalvortex.moneymanager.dto.IncomeDTO;
import com.digitalvortex.moneymanager.dto.RecentTransactionDTO;
import com.digitalvortex.moneymanager.model.Profile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Stream.concat;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ProfileService profileService;

    private final IncomeService incomeService;

    private final ExpenseService expenseService;

    public Map<String, Object> getDashboardData(){
        Profile profile = profileService.getCurrentProfile();
        Map<String, Object> returnValue = new LinkedHashMap<>();

        List<IncomeDTO> incomes = incomeService.latestFiveIncomesForCurrentUser();
        List<ExpenseDTO> expenses = expenseService.latestFiveExpensesForCurrentUser();

        List<RecentTransactionDTO> recentTransactions = concat(incomes.stream().map(income ->
                        RecentTransactionDTO.builder()
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
                expenses.stream().map(expense->
                        RecentTransactionDTO.builder()
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
                )
                .sorted((a,b)->{
                    int compare = b.getDate().compareTo(a.getDate());
                    if (compare == 0 && a.getCreatedAt() != null && b.getCreatedAt() != null) {
                        return b.getCreatedAt().compareTo(a.getCreatedAt());
                    }

                    return compare;
                }).collect(Collectors.toList());
        returnValue.put("totalBalance", incomeService.getTotalIncomeForCurrentUser().subtract(expenseService.getTotalExpensesForCurrentUser()));
        returnValue.put("totalIncome", incomeService.getTotalIncomeForCurrentUser());
        returnValue.put("totalExpenses", expenseService.getTotalExpensesForCurrentUser());
        returnValue.put("recentFiveExpenses", expenses);
        returnValue.put("recentFiveIncomes", incomes);
        returnValue.put("recentTransactions", recentTransactions);

        return returnValue;

    }
}
