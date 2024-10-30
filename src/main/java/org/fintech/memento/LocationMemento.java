package org.fintech.memento;

import lombok.Data;
import org.fintech.store.repos.LocationRepos;

import java.time.LocalDateTime;

@Data
public class LocationMemento {
    private final LocalDateTime date;
    private final LocationRepos locationReposState;

    public LocationMemento(LocationRepos repos, LocalDateTime date) {
        this.date=date;
        locationReposState = new LocationRepos();
        repos.findAll().forEach(locationReposState::save);
    }
}
