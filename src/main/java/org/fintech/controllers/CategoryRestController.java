package org.fintech.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.fintech.config.Timelog;
import org.fintech.dto.CategoryDto;
import org.fintech.services.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@Timelog
@RestController
@RequestMapping("/api/v1/places/categories")
@RequiredArgsConstructor
public class CategoryRestController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> getCategories() {
        return categoryService.findAll();
    }
    @GetMapping("/{id}")
    public CategoryDto getCategory(@NotNull @PathVariable("id")Long id) {
        return categoryService
                .findById(id)
                .orElseThrow(() -> new NoSuchElementException("Category with id: " + id + " not found"));
    }
    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CategoryDto newCategory) {
        CategoryDto category = categoryService.create(newCategory);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(category);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable("id") Long id, @Valid @RequestBody CategoryDto requestCategory) {
        requestCategory.setId(id);
        categoryService.update(id, requestCategory);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable("id") Long id) {

        categoryService.deleteById(id);
        return ResponseEntity.noContent().build();

    }


}
