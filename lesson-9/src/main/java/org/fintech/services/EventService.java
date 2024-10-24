package org.fintech.services;

import org.fintech.dto.EventDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EventService {
    List<EventDto> findAllEventsByFilter(String name, String description, LocalDate dateFrom, LocalDate dateTo, Integer placeId);
    Optional<EventDto> findEventById(int id);
    EventDto createEvent(int place_id, String name, String description, LocalDate date);
    void updateEvent(int id, String name, String description, LocalDate date);
    void deleteEventById(int id);
}
