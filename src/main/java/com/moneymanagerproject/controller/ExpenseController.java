package com.moneymanagerproject.controller;

import com.moneymanagerproject.dto.ExpenseDto;
import com.moneymanagerproject.entity.ExpenseEntity;
import com.moneymanagerproject.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/expenses")
public class ExpenseController {
    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ExpenseDto> addExpense(@RequestBody ExpenseDto expenseDto){
        ExpenseDto expense=expenseService.addExpense(expenseDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(expense);

    }

    @GetMapping
    public ResponseEntity<List<ExpenseDto>> getAllExpensesOfCurrentMonth(){
        return ResponseEntity.ok(
                expenseService.getExpenseOfCurrentMonthorstartDatandendDate()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteExpenseById(@PathVariable Long id){
        expenseService.deleteExpense(id);
        return ResponseEntity.status(HttpStatus.OK).body("Expense has been deleted");
    }

}
