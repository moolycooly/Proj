package org.fintech.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.client.RestClient;


import java.io.IOException;
import java.math.BigDecimal;

@Configuration
public class RestClientConfiguration {
    @Bean
    public RestClient cbrRestClient(@Value("${cbr.api.base-url}") String baseUrl) {
        return RestClient
                .builder()
                .baseUrl(baseUrl)
                .messageConverters(converters -> converters.add(xmlHttpMessageConverter()))
                .build();
    }
    @Bean
    public MappingJackson2XmlHttpMessageConverter xmlHttpMessageConverter() {

        SimpleModule simpleModule = new SimpleModule().addDeserializer(BigDecimal.class, new JsonDeserializer<>() {
                    @Override
                    public BigDecimal deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                        String text = p.getText();
                        if (text != null) {
                            text = text.replace(",", ".");
                            return new BigDecimal(text);
                        }
                        return null;
                    }
                });
        var xmlMapper = new XmlMapper();
        xmlMapper.registerModule(simpleModule);
        return new MappingJackson2XmlHttpMessageConverter(xmlMapper);
    }

}
