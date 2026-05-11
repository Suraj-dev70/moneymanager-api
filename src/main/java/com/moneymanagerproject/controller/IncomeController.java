package com.moneymanagerproject.controller;

import com.moneymanagerproject.dto.ExpenseDto;
import com.moneymanagerproject.dto.IncomeDto;
import com.moneymanagerproject.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping("/incomes")
public class IncomeController {

    private final IncomeService incomeService;

    @PostMapping
   public ResponseEntity<IncomeDto> addIncome(@RequestBody IncomeDto incomeDto){
       IncomeDto addDtoIncome=incomeService.addIncome(incomeDto);
       return ResponseEntity.status(HttpStatus.CREATED).body(addDtoIncome);
   }

    @GetMapping
    public ResponseEntity<List<IncomeDto>> getAllExpensesOfCurrentMonth(){
        return ResponseEntity.ok(
                incomeService.getIncomeOfCurrentMonth()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteIncomeById(@PathVariable Long id){
        incomeService.deleteIncome(id);
        return ResponseEntity.status(HttpStatus.OK).body("Income has been deleted");
    }
}
