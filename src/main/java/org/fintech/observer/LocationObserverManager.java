package org.fintech.observer;

import lombok.RequiredArgsConstructor;
import org.fintech.dto.LocationDto;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LocationObserverManager implements ObserverManager<LocationDto, LocationObserver> {
    private final List<LocationObserver> observers;

    @Override
    public void addObserver(LocationObserver observer) {
        observers.add(observer);
    }
    @Override
    public void removeObserver(LocationObserver observer) {
        observers.remove(observer);
    }
    @Override
    public void notifyObservers(LocalDateTime localDateTime, LocationDto locationDto) {
        for (LocationObserver observer : observers) {
            observer.handleEvent(localDateTime,locationDto);
        }
    }
}
