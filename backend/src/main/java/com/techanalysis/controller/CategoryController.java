package com.techanalysis.controller;

import com.techanalysis.dto.CategoryData;
import com.techanalysis.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<CategoryData> getCategories() {
        CategoryData data = categoryService.getCategoryData();
        return ResponseEntity.ok(data);
    }
}
