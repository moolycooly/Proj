package org.fintech.services;

import org.fintech.dto.EventDto;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface EventService {
    CompletableFuture<List<EventDto>> getEventsCompletableFuture(Double budget, String currencyCode, LocalDate dateFrom, LocalDate dateTo);
    Mono<List<EventDto>> getEventsProjectReactor(Double budget, String currencyCode, LocalDate dateFrom, LocalDate dateTo);

}
