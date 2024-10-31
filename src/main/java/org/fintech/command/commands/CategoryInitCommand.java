package org.fintech.command.commands;

import lombok.RequiredArgsConstructor;
import org.fintech.services.CategoryService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryInitCommand implements DataInitCommand{
    private final CategoryService categoryService;
    @Override
    public void execute() {
        categoryService.init();
    }
}
