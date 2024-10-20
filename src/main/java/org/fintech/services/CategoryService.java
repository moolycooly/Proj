package org.fintech.services;

import org.fintech.services.baseService.CrudService;
import org.fintech.dto.CategoryDto;

public interface CategoryService extends CrudService<CategoryDto, Long> {
    void init();
}
