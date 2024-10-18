package org.fintech.controllers;

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

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
@Slf4j
public class EventController {
    private final EventService eventService;
    @GetMapping

    public List<EventDto> getEvents(
                                 @RequestParam(name="budget") Double budget,
                                 @RequestParam(name="currency") String currencyCode,
                                 @RequestParam(name="dateFrom",required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
                                 @RequestParam(name="dateTo",required = false)  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo)
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
    @RequestMapping("/reactor")
    public List<EventDto> getEventsMono(
            @RequestParam(name="budget") Double budget,
            @RequestParam(name="currency") String currencyCode,
            @RequestParam(name="dateFrom",required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(name="dateTo",required = false)  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo)
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
