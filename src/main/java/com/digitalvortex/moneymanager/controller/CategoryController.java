package com.digitalvortex.moneymanager.controller;

import com.digitalvortex.moneymanager.dto.CategoryDTO;
import com.digitalvortex.moneymanager.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody CategoryDTO categoryDTO){
        CategoryDTO saved = categoryService.saveCategory(categoryDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getCategoriesForUser(){
        List<CategoryDTO> categoriesForCurrentUser = categoryService.getCategoriesForCurrentUser();

        return ResponseEntity.ok(categoriesForCurrentUser);
    }

    @GetMapping("/{type}")
    public ResponseEntity<List<CategoryDTO>> getCategoriesForUser(String type){
       List<CategoryDTO> categoryDTOS = categoryService.getCategoriesByTYpeForCurrentUser(type);

        return ResponseEntity.ok(categoryDTOS);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long categoryId, @RequestBody CategoryDTO categoryDTO){

        CategoryDTO updatedCategory = categoryService.updateCategory(categoryId, categoryDTO);

        return ResponseEntity.ok(updatedCategory);

    }
}
