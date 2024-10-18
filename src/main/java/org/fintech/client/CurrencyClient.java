package org.fintech.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fintech.client.payload.ConvertCurrencyRequest;
import org.fintech.client.payload.ConvertCurrencyResponse;
import org.fintech.config.Timelog;
import org.fintech.exception.ServiceIsNotAvailableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Component
@Slf4j
@RequiredArgsConstructor
@EnableRetry
@Timelog
public class CurrencyClient {
    @Value("${currency.name}")
    private String serviceName;
    @Autowired
    @Qualifier("currencyRestClient")
    private RestClient currencyRestClient;
    @Autowired
    @Qualifier("currencyWebClient")
    private WebClient currencyWebClient;

    @Retryable(
            retryFor = { RuntimeException.class },
            maxAttemptsExpression = "${currency.retry.max-attemps}",
            backoff = @Backoff(delayExpression = "${kudago.retry.delay}")
    )

    public BigDecimal convertCurrency(String from, String to, BigDecimal amount){
        try {
            return currencyRestClient
                    .post()
                    .uri("/convert")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ConvertCurrencyRequest(from, to, amount))
                    .retrieve()
                    .onStatus(HttpStatusCode::is5xxServerError, ((request, response) -> {
                        throw new ServiceIsNotAvailableException(serviceName);
                    }))
                    .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                        var tmp = new String(response.getBody().readAllBytes());
                        throw new ResponseStatusException(response.getStatusCode(), parseDetailsBadRequestResponse(tmp));
                    })
                    .body(ConvertCurrencyResponse.class)
                    .getConvertedAmount();
        }
        catch (ResourceAccessException e){
            throw new ServiceIsNotAvailableException(serviceName);
        }
    }
    @Retryable(
            retryFor = { RuntimeException.class },
            maxAttemptsExpression = "${currency.retry.max-attemps}",
            backoff = @Backoff(delayExpression = "${kudago.retry.delay}")
    )
    public Mono<BigDecimal> convertCurrencyMono(String from, String to, BigDecimal amount){
        return currencyWebClient
                .post()
                .uri("/convert")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(new ConvertCurrencyRequest(from,to,amount)))
                .retrieve()
                .bodyToMono(ConvertCurrencyResponse.class)
                .onErrorResume(Exception.class, ex-> {
                    if(ex instanceof WebClientResponseException.BadRequest) {
                        var tmp = ((WebClientResponseException.BadRequest) ex).getResponseBodyAsString();
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,parseDetailsBadRequestResponse(tmp));
                    }
                    throw new ServiceIsNotAvailableException(serviceName);
                    })
                .map(ConvertCurrencyResponse::getConvertedAmount);
    }
    private String parseDetailsBadRequestResponse(String tmp) {
        String errors = new String();
        int i = 0;
        while(i+9 < tmp.length() && i+9 <= tmp.length()-3) {
            if(tmp.substring(i, i+6).equals("errors")) {
                errors = tmp.substring(i+9,tmp.length()-2);
                break;
            }
            i++;
        }
        return errors.replace("\"","");
    }


}
