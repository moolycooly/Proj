package org.fintech.client;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.fintech.client.payload.EventResponse;
import org.fintech.dto.EventDto;
import org.fintech.exception.ServiceIsNotAvailableException;
import org.fintech.store.entity.CategoryEntity;
import org.fintech.store.entity.LocationEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
@EnableRetry
@Getter
public class KudagoClient {
    @Value("${kudago.name}")
    private String serviceName;
    @Value("${kudago.api.endpoints.get-categories.url}")
    private String urlGetCategories;
    @Value("${kudago.api.endpoints.get-locations.url}")
    private String urlGetLocations;
    @Value("${kudago.api.endpoints.get-events.url}")
    private String urlGetEvents;
    @Autowired
    @Qualifier("kudagoRestClient")
    private RestClient kudagoRestClient;

    @Autowired
    @Qualifier("kudagoWebClient")
    private WebClient kudagoWebClient;

    @Autowired
    @Lazy
    private KudagoClient kudagoClient;


    @Retryable(
            retryFor = { RuntimeException.class },
            maxAttemptsExpression = "${kudago.retry.max-attemps}",
            backoff = @Backoff(delayExpression = "${kudago.retry.delay}")
    )
    public List<LocationEntity> getLocations() {
        log.info("Trying to retrieve locations from Kudago");
        return getDataFromKudago(urlGetLocations, new ParameterizedTypeReference<>() {});
    }
    @Retryable(
            retryFor = { RuntimeException.class },
            maxAttemptsExpression = "${kudago.retry.max-attemps}",
            backoff = @Backoff(delayExpression = "${kudago.retry.delay}")
    )
    public List<CategoryEntity> getCategories() {
        log.info("Trying to retrieve categories from Kudago");
        return getDataFromKudago(urlGetCategories, new ParameterizedTypeReference<>() {});
    }

    public  <T> List<T> getDataFromKudago(String url, ParameterizedTypeReference<List<T>> parameterizedTypeReference){

        List<T> list = kudagoRestClient
                .get()
                .uri(url)
                .retrieve()
                .body(parameterizedTypeReference);
        log.info("KUDAGO API: Data {} was saved",parameterizedTypeReference.getType().getTypeName());
        return list;
    }

    public List<EventDto> getEventsAsync(LocalDate dateFrom, LocalDate dateTo,int pageSize) {
        UriBuilder uriBuilder = UriComponentsBuilder
                .fromPath(urlGetEvents)
                .queryParam("actual_since", dateFrom.toString())
                .queryParam("actual_until", dateTo.toString())
                .queryParam("text_format", "text")
                .queryParam("fields", List.of("id,title,price,is_free,description"))
                .queryParam("location","nsk")
                .queryParam("page_size",pageSize)
                .queryParam("page",1);

        EventResponse responce = kudagoClient.getEventsSyncronized(uriBuilder.toUriString());
        int totalCountPages = (responce.getCount()+pageSize-1)/pageSize;
        List<CompletableFuture<EventResponse>> completableFutures = new ArrayList<>();

        for (int i = 2; i <= totalCountPages; i++){
            int page = i;
            uriBuilder.replaceQueryParam("page",page);
            String url = new String(uriBuilder.toUriString());
            var response = CompletableFuture.supplyAsync(() -> kudagoClient.getEventsSyncronized(url))
                    .exceptionally(ex->null);
            completableFutures.add(response);
        }
        var result= completableFutures.stream()
                .flatMap(future -> {
                    if(future.join()==null || future.join().getResults()==null) {
                        return null;
                    }
                    return future.join().getResults().stream();
                })
                .collect(Collectors.toList());
        result.addAll(responce.getResults());
        return result;
    }
    @Retryable(
            retryFor = { RuntimeException.class },
            maxAttemptsExpression = "${kudago.retry.max-attemps}",
            backoff = @Backoff(delayExpression = "${kudago.retry.delay}")
    )
    public EventResponse getEventsSyncronized(String uri) {
        var response = kudagoRestClient
                .get()
                .uri(uri)
                .retrieve()
                .toEntity(EventResponse.class);
        return response.getBody();
    }

    public Mono<List<EventDto>> getEventsMono(LocalDate dateFrom, LocalDate dateTo,int pageSize) {
        return Flux.range(1,Integer.MAX_VALUE)
                .concatMap(page -> getEventsFromPage(dateFrom, dateTo, page,pageSize))
                .takeWhile(response->!response.getResults().isEmpty())
                .flatMapIterable(EventResponse::getResults)
                .collectList();
    }
    @Retryable(
            retryFor = { RuntimeException.class },
            maxAttemptsExpression = "${kudago.retry.max-attemps}",
            backoff = @Backoff(delayExpression = "${kudago.retry.delay}")
    )
    private Mono<EventResponse> getEventsFromPage(LocalDate dateFrom, LocalDate dateTo, int page,int pageSize) {
        return kudagoWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(urlGetEvents)
                        .queryParam("actual_since", dateFrom.toString())
                        .queryParam("actual_until", dateTo.toString())
                        .queryParam("text_format", "text")
                        .queryParam("fields", List.of("id,title,price,is_free,description"))
                        .queryParam("location","nsk")
                        .queryParam("page",page)
                        .queryParam("page_size",pageSize)
                        .build())
                .retrieve()
                .bodyToMono(EventResponse.class)
                .onErrorResume(WebClientResponseException.NotFound.class, ex->Mono.just(new EventResponse()))
                .onErrorResume(WebClientResponseException.class, ex->Mono.error(new ServiceIsNotAvailableException(serviceName)))
                .onErrorResume(Exception.class, ex->Mono.error(new ServiceIsNotAvailableException(serviceName)) );
    }
    @Recover
    public List<LocationEntity> getLocationsRecovery(RuntimeException e) {
        log.error("Locations was not retrieved {}", e.getMessage());
        return List.of();

    }
    @Recover
    public List<CategoryEntity> getCategoriesRecovery(RuntimeException e) {
        log.error("Categories was not retrieved {}", e.getMessage());
        return List.of();

    }
    @Recover
    public EventResponse getEventsSyncronizedRecovery(RuntimeException e,String uri) {
        return new EventResponse();
    }
}
