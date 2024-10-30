package org.fintech.memento;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Data
public class CategoryCareTaker {

    private final Map<Integer,CategoryMemento> history = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    public void save(CategoryMemento categoryMemento) {
        history.put(counter.incrementAndGet(),categoryMemento);
    }
    public Optional<CategoryMemento> getHistory(LocalDateTime dateFrom, LocalDateTime dateTo) {
        return history.values().stream().filter(a->a.getDate().isAfter(dateFrom) && a.getDate().isBefore(dateTo)).findAny();
    }
    public Optional<CategoryMemento> getHistoryById(int id) {
        return Optional.ofNullable(history.get(id));
    }


}