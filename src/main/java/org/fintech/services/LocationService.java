package org.fintech.services;

import org.fintech.services.baseService.CrudService;
import org.fintech.dto.LocationDto;
import org.springframework.boot.context.event.ApplicationReadyEvent;

import java.time.LocalDateTime;

public interface LocationService extends CrudService<LocationDto, Long> {
    void init(ApplicationReadyEvent event);
    void saveState();
    void restoreState(LocalDateTime dateFrom, LocalDateTime dateTo);
    void restoreState(int id);

}
