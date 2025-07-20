package com.digitalvortex.moneymanager.controller;

import com.digitalvortex.moneymanager.dto.ExpenseDTO;
import com.digitalvortex.moneymanager.dto.FilterDTO;
import com.digitalvortex.moneymanager.dto.IncomeDTO;
import com.digitalvortex.moneymanager.service.ExpenseService;
import com.digitalvortex.moneymanager.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/filter")
public class FilterController {

    private final IncomeService incomeService;

    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<?> filterRecords(@RequestBody FilterDTO filter){
        LocalDate startDate = filter.getStartDate() != null ? filter.getStartDate() : LocalDate.MIN;
        LocalDate endDate = filter.getEndDate() != null ? filter.getEndDate() : LocalDate.now();
        String keyWord = filter.getKeyWord() != null ? filter.getKeyWord() : "";
        String sortField = filter.getSortField() != null ? filter.getSortField() : "date";
        Sort.Direction direction = "desc".equalsIgnoreCase(filter.getSortOrder()) ? Sort.Direction.DESC : Sort.Direction.ASC;

        Sort sort = Sort.by(direction, sortField);

        if ("income".equalsIgnoreCase(filter.getType())) {
            List<IncomeDTO> income = incomeService.filterIncome(startDate, endDate, keyWord, sort);

            return ResponseEntity.ok(income);

        } else if ("expense".equalsIgnoreCase(filter.getType())) {
            List<ExpenseDTO> expenses = expenseService.filterExpenses(startDate, endDate, keyWord, sort);

            return ResponseEntity.ok(expenses);

        }else{
            return ResponseEntity.badRequest().body("Invalid type");
        }

    }
}
