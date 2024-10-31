package org.fintech.command.commands;

import lombok.RequiredArgsConstructor;
import org.fintech.services.LocationService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LocationInitCommand implements DataInitCommand{
    private final LocationService locationService;

    @Override
    public void execute() {
        locationService.init();
    }
}
