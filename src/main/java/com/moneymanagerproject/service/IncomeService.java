package com.moneymanagerproject.service;

import com.moneymanagerproject.dto.ExpenseDto;
import com.moneymanagerproject.dto.IncomeDto;
import com.moneymanagerproject.entity.CategoryEntity;
import com.moneymanagerproject.entity.ExpenseEntity;
import com.moneymanagerproject.entity.IncomeEntity;
import com.moneymanagerproject.entity.ProfileEntity;

import com.moneymanagerproject.repository.CategoryRepository;
import com.moneymanagerproject.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeService {
    private final IncomeRepository incomeRepository;
    private final CategoryRepository categoryRepository;
    private final ProfileService profileService;

    public IncomeDto addIncome(IncomeDto incomeDto) {
        ProfileEntity profileEntity=profileService.getCurrentProfile();
        CategoryEntity categoryEntity=categoryRepository.findById(incomeDto.getCategoryId())
                .orElseThrow(()->new RuntimeException("Category not found"));
        IncomeEntity incomeEntity=toEntity(incomeDto,profileEntity,categoryEntity);
        incomeEntity=incomeRepository.save(incomeEntity);
        return toDto(incomeEntity);
    }

    public List<IncomeDto> getIncomeOfCurrentMonth(){
        ProfileEntity profile=profileService.getCurrentProfile();
        LocalDate now =LocalDate.now();
        LocalDate startDate=now.withDayOfMonth(1);
        LocalDate endDate=now.withDayOfMonth(now.lengthOfMonth());
        List<IncomeEntity> list=incomeRepository.findByProfileIdAndDateBetween(profile.getId(), startDate,endDate);
        return list.stream().map(this::toDto).toList();
    }

    //delete expense
    public void deleteIncome(Long incomeId){
        ProfileEntity profile= profileService.getCurrentProfile();
        IncomeEntity incomeEntity=incomeRepository.findById(incomeId)
                .orElseThrow(()->new RuntimeException("Income not found"));

        if(!incomeEntity.getProfile().getId().equals(profile.getId())){
            throw new RuntimeException("Unauthorized to delete income");
        }
        incomeRepository.delete(incomeEntity);
    }
    //filter incomes
    public List<IncomeDto> filterIncomes(LocalDate startDate, LocalDate endDate, String keyword, Sort sort) {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<IncomeEntity> list = incomeRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profile.getId(), startDate, endDate, keyword, sort);
        return list.stream().map(this::toDto).toList();
    }
    //get atlest 5 expense
    public List<IncomeDto> get5Income(){
        ProfileEntity profile=profileService.getCurrentProfile();
        List<IncomeEntity> list=incomeRepository.findByProfileIdOrderByDateDesc(profile.getId());
        return list.stream().map(this::toDto).toList();
    }
    //get total income of current user
    public BigDecimal totalIncomeOfCurrentUser(){
        ProfileEntity profile=profileService.getCurrentProfile();
        BigDecimal total=incomeRepository.findTotalIncomeByProfileId(profile.getId());
        return total!=null?total:BigDecimal.ZERO;
    }
    //helper methods
    public IncomeEntity toEntity(IncomeDto incomeDto,
                                  ProfileEntity profileEntity,
                                  CategoryEntity categoryEntity) {
        return IncomeEntity.builder()
                .id(incomeDto.getId())
                .name(incomeDto.getName())
                .icon(incomeDto.getIcon())
                .date(incomeDto.getDate())
                .amount(incomeDto.getAmount())
                .createdAt(incomeDto.getCreatedAt())
                .updatedAt(incomeDto.getUpdatedAt())
                .profile(profileEntity)
                .category(categoryEntity)
                .build();
    }

    public IncomeDto toDto(IncomeEntity incomeEntity) {
        return IncomeDto.builder()
                .id(incomeEntity.getId())
                .name(incomeEntity.getName())
                .icon(incomeEntity.getIcon())
                .categoryId(incomeEntity.getCategory()!=null?incomeEntity.getCategory().getId():null)
                .categoryName(incomeEntity.getName()!=null?incomeEntity.getCategory().getName():"N/A")
                .amount(incomeEntity.getAmount())
                .date(incomeEntity.getDate())
                .createdAt(incomeEntity.getCreatedAt())
                .updatedAt(incomeEntity.getUpdatedAt())
                .build();
    }



}
