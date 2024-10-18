package org.fintech.services;

import org.fintech.services.baseService.CrudService;
import org.fintech.dto.LocationDto;

public interface LocationService extends CrudService<LocationDto, Long> {
    void init();
}
