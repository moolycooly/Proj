package org.fintech.services;

import org.fintech.services.baseService.CrudService;
import org.fintech.dto.LocationDto;
import org.springframework.boot.context.event.ApplicationReadyEvent;

public interface LocationService extends CrudService<LocationDto, Long> {
    void init(ApplicationReadyEvent event);
}
