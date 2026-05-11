package com.moneymanagerproject.service;

import com.moneymanagerproject.dto.ExpenseDto;
import com.moneymanagerproject.entity.CategoryEntity;
import com.moneymanagerproject.entity.ExpenseEntity;
import com.moneymanagerproject.entity.ProfileEntity;
import com.moneymanagerproject.repository.CategoryRepository;
import com.moneymanagerproject.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;
    private final ProfileService profileService;

    public ExpenseDto addExpense(ExpenseDto expenseDto) {
        ProfileEntity profileEntity=profileService.getCurrentProfile();
        CategoryEntity categoryEntity=categoryRepository.findById(expenseDto.getCategoryId())
                .orElseThrow(()->new RuntimeException("Category not found"));
        ExpenseEntity expenseEntity=toEntity(expenseDto,profileEntity,categoryEntity);
        expenseEntity=    expenseRepository.save(expenseEntity);
        return toDto(expenseEntity);
    }

    //Retrieve all expenses pf current user based on month or startDate or EndDate

    public List<ExpenseDto> getExpenseOfCurrentMonthorstartDatandendDate(){
       ProfileEntity profile=profileService.getCurrentProfile();
       LocalDate now =LocalDate.now();
       LocalDate startDate=now.withDayOfMonth(1);
       LocalDate endDate=now.withDayOfMonth(now.lengthOfMonth());
       List<ExpenseEntity> list=expenseRepository.findByProfileIdAndDateBetween(profile.getId(), startDate,endDate);
       return list.stream().map(this::toDto).toList();
    }

   //filter expenses
   public List<ExpenseDto> filterExpenses(LocalDate startDate, LocalDate endDate, String keyword, Sort sort){
      ProfileEntity profile=  profileService.getCurrentProfile();
    List<ExpenseEntity> list=  expenseRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profile.getId(), startDate,endDate,keyword,sort);
    return list.stream().map(this::toDto).toList();
   }

   //Notification
    public List<ExpenseDto> getExpenseForUserOnDate(Long profileId,LocalDate date){
        List<ExpenseEntity> list=expenseRepository.findByProfileIdAndDate(profileId,date);
        return list.stream().map(this::toDto).toList();
    }

    //delete expense
    public void deleteExpense(Long expenseId){
       ProfileEntity profile= profileService.getCurrentProfile();
       ExpenseEntity expenseEntity=expenseRepository.findById(expenseId)
               .orElseThrow(()->new RuntimeException("Expense not found"));

       if(!expenseEntity.getProfile().getId().equals(profile.getId())){
           throw new RuntimeException("Unauthorized to delete expense");
       }
       expenseRepository.delete(expenseEntity);
    }

//get atlest 5 expense
    public List<ExpenseDto> get5Expense(){
        ProfileEntity profile=profileService.getCurrentProfile();
        List<ExpenseEntity> list=expenseRepository.findByProfileIdOrderByDateDesc(profile.getId());
        return list.stream().map(this::toDto).toList();
    }
//get total expense of current user
    public BigDecimal totalExpenseOfCurrentUser(){
        ProfileEntity profile=profileService.getCurrentProfile();
        BigDecimal total=expenseRepository.findTotalExpenseByProfileId(profile.getId());
        return total!=null?total:BigDecimal.ZERO;
    }


    //helper methods
    public ExpenseEntity toEntity(ExpenseDto expenseDto,
                                  ProfileEntity profileEntity,
                                  CategoryEntity categoryEntity) {
        return ExpenseEntity.builder()
                .id(expenseDto.getId())
                .name(expenseDto.getName())
                .icon(expenseDto.getIcon())
                .date(expenseDto.getDate())
                .amount(expenseDto.getAmount())
                .createdAt(expenseDto.getCreatedAt())
                .updatedAt(expenseDto.getUpdatedAt())
                .profile(profileEntity)
                .category(categoryEntity)
                .build();
    }

    public ExpenseDto toDto(ExpenseEntity expenseEntity) {
        return ExpenseDto.builder()
                .id(expenseEntity.getId())
                .name(expenseEntity.getName())
                .icon(expenseEntity.getIcon())
                .categoryId(expenseEntity.getCategory()!=null?expenseEntity.getCategory().getId():null)
                .categoryName(expenseEntity.getName()!=null?expenseEntity.getCategory().getName():"N/A")
                .amount(expenseEntity.getAmount())
                .date(expenseEntity.getDate())
                .createdAt(expenseEntity.getCreatedAt())
                .updatedAt(expenseEntity.getUpdatedAt())
                .build();
    }
}
