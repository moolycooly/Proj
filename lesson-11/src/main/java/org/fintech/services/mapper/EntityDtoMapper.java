package org.fintech.services.mapper;

import org.fintech.dto.EventDto;
import org.fintech.dto.PlaceDto;
import org.fintech.store.entity.EventEntity;
import org.fintech.store.entity.PlaceEntity;
import org.springframework.stereotype.Component;

@Component
public class EntityDtoMapper {
    public EventDto mapToEventDto(EventEntity eventEntity) {
        return EventDto.builder()
                .id(eventEntity.getId())
                .placeId(eventEntity.getPlace().getId())
                .name(eventEntity.getName())
                .description(eventEntity.getDescription())
                .date(eventEntity.getDate())
                .build();
    }
    public PlaceDto mapToPlaceDto(PlaceEntity placeEntity) {
        return PlaceDto.builder()
                .id(placeEntity.getId())
                .slug(placeEntity.getSlug())
                .name(placeEntity.getName())
                .build();
    }


}
