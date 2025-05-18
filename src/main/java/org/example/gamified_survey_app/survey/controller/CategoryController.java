package org.example.gamified_survey_app.survey.controller;

import org.example.gamified_survey_app.survey.dto.CategoryDto;
import org.example.gamified_survey_app.survey.repository.CategoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*") // Optional: allow frontend access
public class CategoryController {

    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(cat -> new CategoryDto(cat.getId(), cat.getName()))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public CategoryDto getCategoryById(@PathVariable Long id) {
        return categoryRepository.findById(id)
                .map(cat -> new CategoryDto(cat.getId(), cat.getName()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
    }
}
