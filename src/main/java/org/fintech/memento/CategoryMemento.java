package org.fintech.memento;

import lombok.Data;
import org.fintech.store.repos.CategoryRepos;

import java.time.LocalDateTime;

@Data
public class CategoryMemento {
    private final LocalDateTime date;
    private final CategoryRepos categoryReposState;

    public CategoryMemento(CategoryRepos repos, LocalDateTime date) {
        this.date=date;
        categoryReposState = new CategoryRepos();
        repos.findAll().forEach(categoryReposState::save);
    }
}
