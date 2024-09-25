package org.fintech.config;


import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
@Configuration
public class AppConfig {
    @Bean
    public RestClient kudagoRestClient(@Value("${kudago.api.base-url}") String url) {
        return RestClient.builder()
                .baseUrl(url)
                .build();
    }
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
