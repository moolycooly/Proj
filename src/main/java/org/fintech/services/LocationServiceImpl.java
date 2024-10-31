package org.fintech.services;

import lombok.extern.slf4j.Slf4j;
import org.fintech.client.KudagoClient;
import org.fintech.config.Timelog;
import org.fintech.exception.NoSuchState;
import org.fintech.memento.LocationCareTaker;
import org.fintech.memento.LocationMemento;
import org.fintech.observer.LocationObserver;
import org.fintech.observer.LocationObserverManager;
import org.fintech.services.baseService.CrudServiceImpl;
import org.fintech.dto.LocationDto;
import org.fintech.store.entity.LocationEntity;
import org.fintech.store.repos.LocationRepos;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class LocationServiceImpl extends CrudServiceImpl<LocationEntity, LocationDto, LocationObserver> implements LocationService {

    @Autowired
    private KudagoClient kudagoClient;

    @Autowired
    private LocationCareTaker locationCareTaker;
    public LocationServiceImpl(LocationRepos locationRepos, ModelMapper modelMapper, LocationObserverManager observerManager) {
        super(locationRepos, LocationEntity.class, LocationDto.class, modelMapper,observerManager);
    }
    @Override
    public void saveState() {
        locationCareTaker.save(new LocationMemento((LocationRepos) this.crudRepos, LocalDateTime.now()));
    }
    @Override
    public void restoreState(LocalDateTime dateFrom, LocalDateTime dateTo) {
        var history = locationCareTaker.getHistory(dateFrom,dateTo).orElseThrow(NoSuchState::new);
        this.crudRepos.clear();
        history.getLocationReposState().findAll().forEach(this.crudRepos::save);
    }

    @Override
    public void restoreState(int id) {
        var history = locationCareTaker.getHistoryById(id).orElseThrow(NoSuchState::new);
        this.crudRepos.clear();
        history.getLocationReposState().findAll().forEach(this.crudRepos::save);
    }

    @Override
    @Timelog
    public void init() {
        List<LocationEntity> locations = kudagoClient.getLocations();
        for (LocationEntity location : locations) {
            crudRepos.save(location);
        }
    }
}
