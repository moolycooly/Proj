package org.fintech.services.impl;

import lombok.RequiredArgsConstructor;
import org.fintech.dto.EventDto;
import org.fintech.exception.EventNotFoundException;
import org.fintech.exception.PlaceNotFoundException;
import org.fintech.services.EventService;
import org.fintech.services.mapper.EntityDtoMapper;
import org.fintech.store.entity.EventEntity;
import org.fintech.store.entity.PlaceEntity;
import org.fintech.store.repository.EventRepository;
import org.fintech.store.repository.PlaceRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final PlaceRepository placeRepository;
    private final EntityDtoMapper mapper;

    @Override
    public List<EventDto> findAllEventsByFilter(String name, String description, LocalDate dateFrom, LocalDate dateTo, Integer placeId) {
        PlaceEntity placeEntity = null;
        if(placeId!=null) {
            placeEntity = placeRepository.findById(placeId).orElseThrow(() -> new PlaceNotFoundException(placeId));
        }
        return eventRepository.findAll(EventRepository
                        .buildSpecification(name,description,dateFrom,dateTo,placeEntity)).stream()
                .map(mapper::mapToEventDto)
                .toList();
    }

    @Override
    public Optional<EventDto> findEventById(int id) {
        return eventRepository.findById(id).map(mapper::mapToEventDto);
    }

    @Override
    public EventDto createEvent(int placeId, String name, String description, LocalDate date) {
        PlaceEntity placeEntity = placeRepository.findById(placeId)
                .orElseThrow(()->new PlaceNotFoundException(placeId));
        return mapper.mapToEventDto(eventRepository.save(EventEntity.builder()
                .name(name)
                .description(description)
                .date(date)
                .place(placeEntity)
                .build()));

    }

    @Override
    public void updateEvent(int id, String name, String description, LocalDate date) {
        EventEntity eventEntity = eventRepository.findById(id).orElseThrow(()-> new EventNotFoundException(id));
        if(name!=null) {
            eventEntity.setName(name);
        }
        if(description!=null) {
            eventEntity.setDescription(description);
        }
        if(date!=null) {
            eventEntity.setDate(date);
        }
        eventRepository.save(eventEntity);
    }

    @Override
    public void deleteEventById(int id) {
        eventRepository.findById(id).orElseThrow(()-> new EventNotFoundException(id));
        eventRepository.deleteById(id);
    }
}
