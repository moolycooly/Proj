package org.fintech.services;

import org.fintech.dto.PlaceDto;

import java.util.List;
import java.util.Optional;

public interface PlaceService {

    List<PlaceDto> findAllPlaces();
    Optional<PlaceDto> findPlaceById(int id);
    PlaceDto createPlace(String slug, String name);
    void updatePlace(int id, String slug, String name);
    void deletePlaceById(int id);
}
