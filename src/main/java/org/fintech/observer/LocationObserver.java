package org.fintech.observer;

import lombok.extern.slf4j.Slf4j;

import org.fintech.dto.LocationDto;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class LocationObserver implements Observer<LocationDto> {

    @Override
    public void handleEvent(LocalDateTime time, LocationDto dto) {
        log.info("Got event for location: {} in {}", dto,time);
    }
}
