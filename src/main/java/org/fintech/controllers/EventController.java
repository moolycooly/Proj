package org.fintech.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fintech.dto.EventDto;
import org.fintech.services.EventService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
@Tag(name="Events Controller", description = "Get events filtered by [dateFrom,dateTo], and budget")
@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
@Slf4j
public class EventController {
    private final EventService eventService;

    @Operation(
            summary = "Get events filtered by [dateFrom,dateTo], and budget | Realization: Completable Future",
            description = "returns events in [dateFrom,dateTo] which prices are less than converted budget into RUB or events which are free ",
            responses ={
                    @ApiResponse(responseCode = "200", description = "Success",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventDto.class))),
            }
    )
    @GetMapping
    public List<EventDto> getEvents(
                                 @Schema(type = "number", example = "1255.21") @RequestParam(name="budget") Double budget,
                                 @Schema(example = "USD")  @RequestParam(name="currency") String currencyCode,
                                 @Schema(format = "yyyy-mm-dd",example = "2024-10-21")   @RequestParam(name="dateFrom",required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
                                 @Schema(format = "yyyy-mm-dd",example = "2024-10-25")   @RequestParam(name="dateTo",required = false)  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo)
    {

        if(dateFrom == null && dateTo == null) {
            dateFrom = LocalDate.now().with(DayOfWeek.MONDAY);
            dateTo = LocalDate.now().with(DayOfWeek.SUNDAY);
        }
        else if(dateFrom == null) {
            dateFrom = dateTo.minusDays(7);
        }
        else if(dateTo == null) {
            dateTo = dateFrom.plusDays(7);
        }
        if(dateFrom.isAfter(dateTo)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "dateFrom must be earlier then dateTo"
            );
        }
        return eventService.getEventsCompletableFuture(budget, currencyCode, dateFrom, dateTo).join();
    }
    @Operation(
            summary = "Get events filtered by [dateFrom,dateTo], and budget | Realization: Project Reactor",
            description = "returns events in [dateFrom,dateTo] which prices are less than converted budget into RUB or events which are free ",
            responses ={
                    @ApiResponse(responseCode = "200", description = "Success",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventDto.class))),
            }
    )
    @GetMapping("/reactor")
        public List<EventDto> getEventsMono(
            @Schema(type = "number",example = "1255.21") @RequestParam(name="budget") Double budget,
            @Schema(example = "USD")  @RequestParam(name="currency") String currencyCode,
            @Schema(format = "yyyy-mm-dd",example = "2024-10-21")   @RequestParam(name="dateFrom",required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @Schema(format = "yyyy-mm-dd",example = "2024-10-25")   @RequestParam(name="dateTo",required = false)  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo)
    {

        if(dateFrom == null && dateTo == null) {
            dateFrom = LocalDate.now().with(DayOfWeek.MONDAY);
            dateTo = LocalDate.now().with(DayOfWeek.SUNDAY);
        }
        else if(dateFrom == null) {
            dateFrom = dateTo.minusDays(7);
        }
        else if(dateTo == null) {
            dateTo = dateFrom.plusDays(7);
        }
        if(dateFrom.isAfter(dateTo)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "dateFrom must be earlier then dateTo"
            );
        }
        return eventService.getEventsProjectReactor(budget, currencyCode, dateFrom, dateTo).block();
    }
}
