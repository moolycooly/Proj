package org.fintech.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.fintech.MainLessonSeven;
import org.fintech.controllers.payload.ConvertCurrencyRequest;
import org.junit.jupiter.api.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wiremock.integrations.testcontainers.WireMockContainer;


import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {MainLessonSeven.class})
@Testcontainers
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@RequiredArgsConstructor
@DisplayName("CurrencyRestController integration test, testing when Central Bank API doesn`t work")
public class CurrencyRestControllerTestCbrIsNotAvailable{
    private static final String url = "/currencies";
    @Container
    static WireMockContainer wireMockContainer = new WireMockContainer("wiremock/wiremock:2.35.0");

    @DynamicPropertySource
    static void wireMockProperties(DynamicPropertyRegistry registry) {
        registry.add("cbr.api.base-url", wireMockContainer::getBaseUrl);
    }
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Test
    void getCurrency_CbrIsNotAvailable_ReturnsValidResponseEntity() throws Exception {
        //given
        var requestBuilder = get(url + "/rates/" + "USD");
        //when
        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        //then
                        status().isServiceUnavailable(),
                        content().contentType(MediaType.APPLICATION_JSON));

    }
    @Test
    void convertCurrency_PayloadIsValid_ReturnsValidResponseEntity() throws Exception {
        //given
        String fromCurrency = "USD";
        String toCurrency = "EUR";
        BigDecimal rate = new BigDecimal(100.12);

        var requestBuilder = post(url + "/convert")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        new ConvertCurrencyRequest(fromCurrency, toCurrency, rate)));

        //when
        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        //then
                        status().isServiceUnavailable(),
                        content().contentType(MediaType.APPLICATION_JSON));
    }


}
