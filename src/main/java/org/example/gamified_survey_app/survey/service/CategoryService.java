package org.example.gamified_survey_app.survey.service;


import org.example.gamified_survey_app.survey.model.Category;
import org.example.gamified_survey_app.survey.repository.CategoryRepository;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {
    CategoryRepository categoryRepository;
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    public void createCategory(String name, String description) {
        if (categoryRepository.findByName(name) == null) {
            Category category = new Category();
            category.setName(name);
            category.setDescription(description);
            categoryRepository.save(category);
        } else {
            throw new RuntimeException("Category already exists");
        }
    }
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
    }
    public Category getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }
    public void updateCategory(Long id, String name, String description) {
        Category category = getCategoryById(id);
        category.setName(name);
        category.setDescription(description);
        categoryRepository.save(category);
    }
}
