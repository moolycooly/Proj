package org.fintech.observer;

import lombok.extern.slf4j.Slf4j;
import org.fintech.dto.CategoryDto;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
@Component
@Slf4j
public class CategoryObserver implements Observer<CategoryDto>{

    @Override
    public void handleEvent(LocalDateTime time, CategoryDto dto) {
        log.info("Got event for category: {} in {}", dto,time);
    }
}
