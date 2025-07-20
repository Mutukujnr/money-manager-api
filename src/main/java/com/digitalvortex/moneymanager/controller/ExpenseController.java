package com.digitalvortex.moneymanager.controller;

import com.digitalvortex.moneymanager.dto.ExpenseDTO;
import com.digitalvortex.moneymanager.model.Expense;
import com.digitalvortex.moneymanager.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private  final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ExpenseDTO> addExpense(@RequestBody ExpenseDTO expenseDTO){

        ExpenseDTO eXpense = expenseService.addEXpense(expenseDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(eXpense);
    }

    @GetMapping
    public ResponseEntity<List<ExpenseDTO>> getExpenses(){
        List<ExpenseDTO> expenseDTOS = expenseService.expensesForCurrentUser();

        return ResponseEntity.ok(expenseDTOS);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id){
        expenseService.deleteExpenseForCurrentUser(id);

        return ResponseEntity.noContent().build();

    }
}
