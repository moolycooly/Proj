package org.fintech.services;

import lombok.extern.slf4j.Slf4j;
import org.fintech.client.KudagoClient;
import org.fintech.config.Timelog;
import org.fintech.services.baseService.CrudServiceImpl;
import org.fintech.dto.LocationDto;
import org.fintech.store.entity.LocationEntity;
import org.fintech.store.repos.LocationRepos;
import org.modelmapper.ModelMapper;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class LocationServiceImpl extends CrudServiceImpl<LocationEntity, LocationDto> implements LocationService {

    private final KudagoClient kudagoClient;
    public LocationServiceImpl(LocationRepos locationRepos, ModelMapper modelMapper, KudagoClient kudagoClient) {
        super(locationRepos, LocationEntity.class, LocationDto.class, modelMapper);
        this.kudagoClient = kudagoClient;
    }
    @Override
    @Timelog
    @EventListener(ApplicationReadyEvent.class)
    public void init(ApplicationReadyEvent event) {
        List<LocationEntity> locations = kudagoClient.getLocations();
        for (LocationEntity location : locations) {
            crudRepos.save(location);
        }
    }
}
