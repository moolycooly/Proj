package org.fintech.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fintech.store.entity.CategoryEntity;
import org.fintech.store.entity.LocationEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
@Component
@RequiredArgsConstructor
@Slf4j
@EnableRetry
public class KudagoClient {
    @Value("${kudago.api.endpoints.get-categories.url}")
    private String urlGetCategories;
    @Value("${kudago.api.endpoints.get-locations.url}")
    private String urlGetLocations;
    private final RestClient kudagoRestClient;
    @Retryable(
            retryFor = { RuntimeException.class },
            maxAttempts = 5,
            backoff = @Backoff(delay=800)
    )
    public List<LocationEntity> getLocations() {
        log.info("Trying to retrieve locations from Kudago");
        return getDataFromKudago(urlGetLocations, new ParameterizedTypeReference<>() {});
    }
    @Retryable(
            retryFor = { RuntimeException.class },
            maxAttempts = 5,
            backoff = @Backoff(delay=800)
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
}
