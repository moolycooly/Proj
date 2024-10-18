package org.fintech.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fintech.client.CurrencyClient;
import org.fintech.client.KudagoClient;
import org.fintech.config.Timelog;
import org.fintech.dto.EventDto;
import org.fintech.services.EventService;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
@Timelog
public class EventServiceImpl implements EventService {

    private final CurrencyClient currencyClient;
    private final KudagoClient kudagoClient;

    @Override
    public CompletableFuture<List<EventDto>> getEventsCompletableFuture(Double budget, String currencyCode,
                                    LocalDate dateFrom, LocalDate dateTo) {

        var currentEvents = CompletableFuture.supplyAsync(()->kudagoClient.getEventsAsync(dateFrom,dateTo));
        var currentBudget = CompletableFuture.supplyAsync(()->currencyClient.convertCurrency(currencyCode,"RUB", BigDecimal.valueOf(budget)));

        CompletableFuture<List<EventDto>> future = new CompletableFuture<>();
        currentEvents
                .thenAcceptBoth(currentBudget,(tmpList,tmpBudget)-> {
                    var events = tmpList.stream()
                            .filter((event) -> (event.getIsFree() || (event.getPrice() != null && event.getPrice().compareTo(tmpBudget.intValue()) <= 0)))
                            .toList();
                    future.complete(events);
                }).exceptionally(ex->{
                    future.completeExceptionally(ex);
                    return null;
                });

        return future;
    }

    @Override
    public Mono<List<EventDto>> getEventsProjectReactor(Double budget, String currencyCode, LocalDate dateFrom, LocalDate dateTo) {
        return Mono.zip(
                currencyClient.convertCurrencyMono(currencyCode, "RUB", BigDecimal.valueOf(budget)),
                kudagoClient.getEventsMono(dateFrom,dateTo)
        ).map(tuple -> {
            BigDecimal tmpBudget = tuple.getT1();
            List<EventDto> events = tuple.getT2();

            return events.stream()
                    .filter((event) -> (event.getIsFree() || (event.getPrice() != null && event.getPrice().compareTo(tmpBudget.intValue()) <= 0)))
                    .toList();
        });
    }
}
