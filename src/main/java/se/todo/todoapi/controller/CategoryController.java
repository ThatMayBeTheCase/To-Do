package se.todo.todoapi.controller;

import org.springframework.web.bind.annotation.*;
import se.todo.todoapi.dto.CategoryResponse;
import se.todo.todoapi.dto.CreateCategoryRequest;
import se.todo.todoapi.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public CategoryResponse createCategory(@RequestBody CreateCategoryRequest request) {
        return categoryService.createCategory(request);
    }

    @GetMapping
    public List<CategoryResponse> getAllCategories() {
        return categoryService.getAllCategories();
    }
}
