package com.moneymanagerproject.controller;

import com.moneymanagerproject.dto.ExpenseDto;
import com.moneymanagerproject.dto.FilterDto;
import com.moneymanagerproject.dto.IncomeDto;
import com.moneymanagerproject.service.ExpenseService;
import com.moneymanagerproject.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/filter")
@RequiredArgsConstructor
public class FilterController {

    private final IncomeService incomeService;
    private final ExpenseService expenseService;

    @GetMapping
    public ResponseEntity<?> filter(@RequestBody FilterDto filterDto){
        LocalDate startDate=filterDto.getStartDate()!=null?filterDto.getStartDate():LocalDate.MIN;
        LocalDate endDate=filterDto.getEndDate()!=null?filterDto.getEndDate():LocalDate.now();
        String keyword=filterDto.getKeyword()!=null?filterDto.getKeyword():"";
        String sortField=filterDto.getSortField()!=null?filterDto.getSortField():"date";
        Sort.Direction direction="desc".equalsIgnoreCase(filterDto.getSortOrder())?Sort.Direction.DESC:Sort.Direction.ASC;
        Sort sort=Sort.by(direction,sortField);
        if("income".equals(filterDto.getType())){
         List<IncomeDto> incomes= incomeService.filterIncomes(startDate,endDate,keyword,sort);
         return ResponseEntity.ok(incomes);
        }else if("expense".equals(filterDto.getType())){
          List<ExpenseDto> expenses=  expenseService.filterExpenses(startDate,endDate,keyword,sort);
          return ResponseEntity.ok(expenses);
        }else{
            return ResponseEntity.badRequest().body("Invalid filter type. only 'income' or 'expense' are allowed.");
        }
    }

}
