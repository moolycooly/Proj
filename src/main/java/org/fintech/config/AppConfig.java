package org.fintech.config;


import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Configuration
public class AppConfig {
    @Bean
    public RestClient kudagoRestClient(@Value("${kudago.api.base-url}") String url) {
        return RestClient.builder()
                .baseUrl(url)
                .requestFactory(new HttpComponentsClientHttpRequestFactory())
                .build();
    }
    @Bean
    public RestClient currencyRestClient(@Value("${currency.api.base-url}") String url) {
        return RestClient.builder()
                .baseUrl(url)
                .build();
    }
    @Bean
    public WebClient kudagoWebClient(@Value("${kudago.api.base-url}") String url) {
        return WebClient.create(url);
    }
    @Bean
    public WebClient currencyWebClient(@Value("${currency.api.base-url}") String url) {
        return WebClient.create(url);
    }
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
