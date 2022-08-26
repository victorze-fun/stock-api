package com.victorze.stock.controllers;

import com.victorze.stock.errors.CategoryNotFoundException;
import com.victorze.stock.models.Category;
import com.victorze.stock.repositories.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class CategoryController {

    private final CategoryRepository categoryRepository;

    @GetMapping("/categories")
    public ResponseEntity<?> getAll() {
        var categories = categoryRepository.findAll();

        if (categories.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(categories);
        }
    }

    @GetMapping("/categories/{id}")
    public Category getCategory(@PathVariable Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException(id));
    }

    @PostMapping("/categories")
    public ResponseEntity<Category> newCategory(@RequestBody Category category) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryRepository.save(category));
    }

    @PutMapping("/categories/{id}")
    public Category editCategory(@PathVariable Long id, @RequestBody Category category) {
        return categoryRepository.findById(id).map(c -> {
            c.setName(category.getName());
            return categoryRepository.save(c);
        }).orElseThrow(() -> new CategoryNotFoundException(id));
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        categoryRepository.delete(category);
        return ResponseEntity.noContent().build();
    }

}
