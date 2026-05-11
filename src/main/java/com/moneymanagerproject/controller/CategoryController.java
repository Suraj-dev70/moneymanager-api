package com.moneymanagerproject.controller;

import com.moneymanagerproject.dto.CategoryDto;
import com.moneymanagerproject.entity.CategoryEntity;
import com.moneymanagerproject.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryDto> saveCategory(@RequestBody CategoryDto categoryDto){
        CategoryDto savedCategoryDto = categoryService.saveCategory(categoryDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCategoryDto);
    }

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategories(){
        List<CategoryDto> categories=categoryService.getCategoriesForCurrentUser();
        return ResponseEntity.status(HttpStatus.OK).body(categories);
    }

    @GetMapping("/{type}")
    public ResponseEntity<List<CategoryDto>> getAllCategoriesByTypeAndProfileId(@PathVariable String type){
        List<CategoryDto> categories=categoryService.getCategoriesByType(type);
        return ResponseEntity.status(HttpStatus.OK).body(categories);
    }
    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long categoryId,@RequestBody CategoryDto categoryDto){
        CategoryDto updatedCategoryDto=categoryService.updateCategory(categoryId,categoryDto);
        return ResponseEntity.status(HttpStatus.OK).body(updatedCategoryDto);
    }
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> deleteCategory(@PathVariable Long categoryId){
        CategoryDto deletedCategoryDto=categoryService.deleteCategoryById(categoryId);
        return ResponseEntity.status(HttpStatus.OK).body(deletedCategoryDto);
    }
}

