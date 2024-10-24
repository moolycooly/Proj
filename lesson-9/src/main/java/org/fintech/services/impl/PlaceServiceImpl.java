package org.fintech.services.impl;

import lombok.RequiredArgsConstructor;
import org.fintech.dto.PlaceDto;
import org.fintech.exception.PlaceNotFoundException;
import org.fintech.services.PlaceService;
import org.fintech.services.mapper.EntityDtoMapper;
import org.fintech.store.entity.PlaceEntity;
import org.fintech.store.repository.PlaceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlaceServiceImpl implements PlaceService {
    private final PlaceRepository placeRepository;
    private final EntityDtoMapper mapper;
    @Override
    public List<PlaceDto> findAllPlaces() {
        return placeRepository.findAll().stream().map(mapper::mapToPlaceDto).toList();

    }

    @Override
    public Optional<PlaceDto> findPlaceById(int id) {
        return placeRepository.findById(id).map(mapper::mapToPlaceDto);
    }

    @Override
    public PlaceDto createPlace(String slug, String name) {
        return mapper.mapToPlaceDto(placeRepository.save(PlaceEntity.builder()
                .name(name)
                .slug(slug)
                .build()));
    }

    @Override
    public void updatePlace(int id, String slug, String name) {
        PlaceEntity placeEntity = placeRepository.findById(id).orElseThrow(()->new PlaceNotFoundException(id));
        if(slug!=null) {
            placeEntity.setSlug(slug);
        }
        if(name != null) {
            placeEntity.setName(name);
        }
        placeRepository.save(placeEntity);

    }

    @Override
    public void deletePlaceById(int id) {
        placeRepository.findById(id).orElseThrow(() -> new PlaceNotFoundException(id));
        placeRepository.deleteById(id);

    }
}
