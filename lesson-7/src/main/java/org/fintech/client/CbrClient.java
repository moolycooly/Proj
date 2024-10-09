package org.fintech.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fintech.dto.ValCurs;
import org.fintech.dto.Valute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
@EnableCaching
public class CbrClient {
    private final RestClient restClient;

    @Value("${cbr.api.endpoints.get-rate.url}")
    private String urlGetValCurs;
    @Autowired
    @Lazy
    private CbrClient cbrClient;

    @Cacheable("get-val-curs")
    @CircuitBreaker(name="cbr-client",fallbackMethod = "getValCursFallback")
    public Optional<ValCurs> getValCurs() {
        ResponseEntity<ValCurs> valCurs = restClient
                .get()
                .uri(urlGetValCurs)
                .retrieve().toEntity(ValCurs.class);
        if(valCurs.getStatusCode().is2xxSuccessful() && valCurs.getBody() != null) {
            valCurs.getBody().getValuteList().add(getRubValute());
            return Optional.of(valCurs.getBody());
        }
        return Optional.empty();
    }
    public Optional<Valute> getValuteByCode(String code) {
        var valCurs = cbrClient.getValCurs();  // если вызывать через this, то никакого аоп и кеша не будет
        return valCurs.map(valCursTmp -> valCursTmp.getValuteList()
                        .stream()
                        .filter(valute -> valute.getCharCode().equals(code))
                        .findFirst())
                .orElse(Optional.empty());
    }
    private Optional<ValCurs> getValCursFallback(Exception e) {
        log.error("Cbr is not available: {}", e.getMessage());
        return Optional.empty();
    }
    public Valute getRubValute() {
        return Valute.builder()
                .charCode("RUB")
                .numCode(643)
                .nominal(1)
                .name("Российский рубль")
                .rate(new BigDecimal(1))
                .build();
    }

}
