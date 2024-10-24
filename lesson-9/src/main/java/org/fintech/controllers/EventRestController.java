package org.fintech.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fintech.controllers.payload.*;
import org.fintech.dto.EventDto;
import org.fintech.controllers.payload.EventFilter;
import org.fintech.exception.EventNotFoundException;
import org.fintech.services.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/event")
public class EventRestController {
    private final EventService eventService;

    @GetMapping
    public List<EventDto> getEventsByFilters(EventFilter filter) {
        if(filter.getToDate()!=null&&filter.getToDate().isBefore(filter.getFromDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"toDate must be after fromDate");
        }
        return eventService.findAllEventsByFilter(filter.getName(),filter.getDescription(),filter.getFromDate(),filter.getToDate(), filter.getPlaceId());

    }
    @GetMapping("/{id}")
    public EventDto getEventById(@PathVariable(name = "id") int id) {
        return eventService.findEventById(id).orElseThrow(() -> new EventNotFoundException(id));
    }
    @PostMapping
    public ResponseEntity<EventDto> createEvent(@RequestBody @Valid NewEventPayload newEvent) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(eventService.createEvent(newEvent.placeId(),newEvent.name(),newEvent.description(),newEvent.date()));
    }
    @PutMapping("/{id}")
    public void updateEvent(@RequestBody @Valid UpdateEventPayload updateEvent, @PathVariable(name = "id") int id) {
        eventService.updateEvent(id, updateEvent.name(), updateEvent.description(), updateEvent.date());
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable(name = "id") int id) {
        eventService.deleteEventById(id);
        return ResponseEntity.noContent().build();
    }
}
