package com.moneymanagerproject.service;


import com.moneymanagerproject.dto.CategoryDto;
import com.moneymanagerproject.entity.CategoryEntity;
import com.moneymanagerproject.entity.ProfileEntity;
import com.moneymanagerproject.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final ProfileService profileService;

    //save category
    public CategoryDto saveCategory(CategoryDto categoryDto) {
     ProfileEntity profile=profileService.getCurrentProfile();
        if(categoryRepository.existsByNameAndProfileId(categoryDto.getName(),profile.getId())){
            throw new RuntimeException("Category with this name is already exist");
        }
       CategoryEntity categoryEntity =toEntity(categoryDto,profile);
      categoryEntity=  categoryRepository.save(categoryEntity);
      return toDto(categoryEntity);
    }

//get categories for current user
    public List<CategoryDto> getCategoriesForCurrentUser(){
        ProfileEntity profile=profileService.getCurrentProfile();
        List<CategoryEntity> categories=categoryRepository.findByProfileId(profile.getId());
        return categories.stream().map(this::toDto).toList();
    }

    //update Categories for Currnt user
    public CategoryDto updateCategory(Long categoryId,CategoryDto categoryDto) {
        ProfileEntity profile=profileService.getCurrentProfile();
        CategoryEntity existingCategory=categoryRepository.findByIdAndProfileId(categoryId,profile.getId())
                .orElseThrow(()->new RuntimeException("Category with this id is not exist"));
        existingCategory.setName(categoryDto.getName());
        existingCategory.setIcon(categoryDto.getIcon());
        existingCategory= categoryRepository.save(existingCategory);
        return toDto(existingCategory);
    }
    //delete category
    public CategoryDto deleteCategoryById(Long categoryId){
        ProfileEntity profile=profileService.getCurrentProfile();
        CategoryEntity existCategory=categoryRepository.findByIdAndProfileId(categoryId, profile.getId())
                .orElseThrow(()->new RuntimeException("Category with this id is not exist"));
        categoryRepository.delete(existCategory);
        return toDto(existCategory);
    }
//get categories by type
    public List<CategoryDto> getCategoriesByType(String type){
      ProfileEntity profile= profileService.getCurrentProfile();
      List<CategoryEntity> categories=categoryRepository.findByTypeAndProfileId(type,profile.getId());
      return categories.stream().map(this::toDto).toList();
    }


    //helper methods
    //dto to entity
    private CategoryEntity toEntity(CategoryDto categoryDto, ProfileEntity profile){
        return CategoryEntity.builder()
                .name(categoryDto.getName())
                .icon(categoryDto.getIcon())
                .profile(profile)
                .type(categoryDto.getType())
                .build();
    }

    //entity to dto
    private CategoryDto toDto(CategoryEntity categoryEntity){
        return CategoryDto.builder()
                .id(categoryEntity.getId())
                .profileId(categoryEntity.getProfile()!=null?categoryEntity.getProfile().getId():null)
                .name(categoryEntity.getName())
                .icon(categoryEntity.getIcon())
                .type(categoryEntity.getType())
                .createdAt(categoryEntity.getCreatedAt())
                .updatedAt(categoryEntity.getUpdatedAt())
                .build();
    }
}