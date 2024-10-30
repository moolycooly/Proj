package org.fintech.controllers;

import lombok.RequiredArgsConstructor;
import org.fintech.dto.CategoryMementoDto;
import org.fintech.memento.CategoryCareTaker;
import org.fintech.services.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/state/category")
@RequiredArgsConstructor
public class CategoryStateRestController {
    private final CategoryService categoryService;
    private final CategoryCareTaker categoryCareTaker;
    @GetMapping
    public List<CategoryMementoDto> getStatesTime() {
        return categoryCareTaker.getHistory().entrySet().stream().map(v->new CategoryMementoDto(v.getKey(),v.getValue().getDate())).toList();
    }
    @PostMapping
    public ResponseEntity<?> saveCategoryState() {
        categoryService.saveState();
        return ResponseEntity.ok("State was saved");
    }
    @GetMapping("/restore")
    public ResponseEntity<?> restoreCategoryState(@RequestParam("dateFrom") LocalDateTime dateFrom, @RequestParam("dateTo") LocalDateTime dateTo) {
        categoryService.restoreState(dateFrom,dateTo);
        return ResponseEntity.ok("State was restored");
    }
    @GetMapping("/restore/{id}")
    public ResponseEntity<?> restoreCategoryState(@PathVariable("id") int id) {
        categoryService.restoreState(id);
        return ResponseEntity.ok("State was restored");
    }
}
