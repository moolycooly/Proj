package org.fintech.observer;

import org.fintech.dto.AbstractDto;

import java.time.LocalDateTime;

public interface ObserverManager<D extends AbstractDto, O extends Observer<D>>{
    void addObserver(O observer);
    void removeObserver(O observer);
    void notifyObservers(LocalDateTime localDateTime, D dto);
}
