package org.fintech.services;

import org.fintech.services.baseService.CrudService;
import org.fintech.dto.CategoryDto;
import org.springframework.boot.context.event.ApplicationReadyEvent;

public interface CategoryService extends CrudService<CategoryDto, Long> {
    void init(ApplicationReadyEvent event);
}
