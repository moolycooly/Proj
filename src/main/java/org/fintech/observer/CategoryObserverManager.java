package org.fintech.observer;

import lombok.RequiredArgsConstructor;
import org.fintech.dto.CategoryDto;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Component
public class CategoryObserverManager implements ObserverManager<CategoryDto, CategoryObserver> {

    private final List<CategoryObserver> observers;
    @Override
    public void addObserver(CategoryObserver observer) {
        observers.add(observer);
    }
    public void removeObserver(CategoryObserver observer) {
        observers.remove(observer);
    }
    public void notifyObservers(LocalDateTime localDateTime, CategoryDto categoryDto) {
        for (CategoryObserver observer : observers) {
            observer.handleEvent(localDateTime,categoryDto);
        }
    }
}
