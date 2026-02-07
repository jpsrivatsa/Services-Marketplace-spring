package com.example.assistio.service;
import com.example.assistio.model.Category;
import com.example.assistio.model.User;
import com.example.assistio.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    public List<Category> getAllCategories(User user) {
        if (user == null) {
            throw new SecurityException("Access denied: User not authenticated.");
        }
        return categoryRepository.findAll();
    }
    public Category getCategoryById(Long categoryId, User user) {
        if (user == null) {
            throw new SecurityException("Access denied: User not authenticated.");
        }
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + categoryId));
    }
    public Category getCategoryByShortName(String shortName, User user) {
        if (user == null) {
            throw new SecurityException("Access denied: User not authenticated.");
        }
        return categoryRepository.findByShortName(shortName)
                .orElseThrow(() -> new RuntimeException("Category not found with short name: " + shortName));
    }
    public List<Category> filterCategoriesByActiveStatus(boolean isActive, User user) {
        if (user == null) {
            throw new SecurityException("Access denied: User not authenticated.");
        }
        return categoryRepository.findAll().stream()
                .filter(category -> category.isActive() == isActive)
                .collect(Collectors.toList());
    }
    public void updateCategory(Category category, User user) {
        if (user == null || !user.getRole().getName().equalsIgnoreCase("admin")) {
            throw new SecurityException("Access denied: Only admins can update categories.");
        }
        categoryRepository.updateCategory(category);
    }
    public void CreateCategory(Category category, User user){
        if (user == null || !user.getRole().getName().equalsIgnoreCase("admin")) {
            throw new SecurityException("Access denied: Only admins can create categories.");
        }
        categoryRepository.saveCategory(category);
    }

}
