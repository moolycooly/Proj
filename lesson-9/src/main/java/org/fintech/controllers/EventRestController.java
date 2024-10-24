package org.fintech.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fintech.controllers.payload.*;
import org.fintech.dto.EventDto;
import org.fintech.controllers.payload.EventFilter;
import org.fintech.exception.EventNotFoundException;
import org.fintech.services.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/event")
@Tag(name="Events Controller")
public class EventRestController {
    private final EventService eventService;

    @Operation(
            summary = "Get events by filter",
            responses ={
                    @ApiResponse(responseCode = "200", description = "Success",
                            content = @Content(mediaType = "application/json", array=@ArraySchema(schema = @Schema(implementation = EventDto.class))))
            }
    )

    @GetMapping
    public List<EventDto> getEventsByFilters(EventFilter filter) {
        if(filter.getToDate()!=null&&filter.getToDate().isBefore(filter.getFromDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"toDate must be after fromDate");
        }
        return eventService.findAllEventsByFilter(filter.getName(),filter.getDescription(),filter.getFromDate(),filter.getToDate(), filter.getPlaceId());

    }
    @Operation(
            summary = "Get event by id",
            responses ={
                    @ApiResponse(responseCode = "200", description = "Success",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = EventDto.class))),
                    @ApiResponse(responseCode = "404", description = "Event was not found",
                            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class))),
            }
    )
    @GetMapping("/{id}")
    public EventDto getEventById(@PathVariable(name = "id") int id) {
        return eventService.findEventById(id).orElseThrow(() -> new EventNotFoundException(id));
    }
    @Operation(
            summary = "Create event",
            responses ={
                    @ApiResponse(responseCode = "200", description = "Success",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = EventDto.class))),
                    @ApiResponse(responseCode = "404", description = "Place was not found",
                            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid payload",
                            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class))),
            }
    )
    @PostMapping
    public ResponseEntity<EventDto> createEvent(@RequestBody @Valid NewEventPayload newEvent) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(eventService.createEvent(newEvent.placeId(),newEvent.name(),newEvent.description(),newEvent.date()));
    }
    @Operation(
            summary = "Update event by id",
            responses ={
                    @ApiResponse(responseCode = "200", description = "Success",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = EventDto.class))),
                    @ApiResponse(responseCode = "404", description = "Event was not found",
                            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @PutMapping("/{id}")
    public void updateEvent(@RequestBody @Valid UpdateEventPayload updateEvent, @PathVariable(name = "id") int id) {
        eventService.updateEvent(id, updateEvent.name(), updateEvent.description(), updateEvent.date());
    }
    @Operation(
            summary = "Delete event by id",
            responses ={
                    @ApiResponse(responseCode = "204", description = "Success",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = void.class))),
                    @ApiResponse(responseCode = "404", description = "Event was not found",
                            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable(name = "id") int id) {
        eventService.deleteEventById(id);
        return ResponseEntity.noContent().build();
    }
}
