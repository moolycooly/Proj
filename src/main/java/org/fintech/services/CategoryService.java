package org.fintech.services;

import org.fintech.services.baseService.CrudService;
import org.fintech.dto.CategoryDto;

import java.time.LocalDateTime;

public interface CategoryService extends CrudService<CategoryDto, Long> {
    void init();
    void saveState();
    void restoreState(LocalDateTime dateFrom, LocalDateTime dateTo);
    void restoreState(int id);

}
