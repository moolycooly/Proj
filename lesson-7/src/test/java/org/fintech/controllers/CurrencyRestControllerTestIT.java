package org.fintech.controllers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import org.fintech.MainLessonSeven;
import org.fintech.controllers.payload.ConvertCurrencyRequest;
import org.fintech.dto.ConvertCurrencyDto;
import org.fintech.dto.CurrencyDto;
import org.fintech.dto.ValCurs;
import org.fintech.dto.Valute;
import org.junit.jupiter.api.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
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

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import static java.lang.Math.min;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {MainLessonSeven.class})
@Testcontainers
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@RequiredArgsConstructor
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CurrencyRestControllerTestIT {
    private static final String url = "/currencies";
    @Container
    static WireMockContainer wireMockContainer = new WireMockContainer("wiremock/wiremock:2.35.0")
            .withFileFromResource("cbr_val_curs.xml","org/fintech/controllers/cbr_val_curs.xml")
            .withMappingFromResource("cbr_request_response.json", CurrencyRestControllerTestIT.class, "cbr_request_response.json");
    @DynamicPropertySource
    static void wireMockProperties(DynamicPropertyRegistry registry) {
        registry.add("cbr.api.base-url", wireMockContainer::getBaseUrl);
    }
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private static XmlMapper xmlMapper;
    private static ValCurs valCurs;

    @BeforeAll
    static void setUp()  {
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
        xmlMapper = new XmlMapper();
        xmlMapper.setDefaultUseWrapper(false);
        xmlMapper.registerModule(simpleModule);

        try(InputStream inputStream = CurrencyRestControllerTestIT.class
                .getResourceAsStream("cbr_val_curs.xml")){
            valCurs = xmlMapper.readValue(inputStream, ValCurs.class);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @ParameterizedTest
    @MethodSource("validCurrencyCodes")
    void getCurrencyRate_CurrencyExists_ReturnsRate(String codeValute) throws Exception {
        //given
        CurrencyDto currencyDto =valCurs
                .getValuteList()
                .stream()
                .filter((valute)->valute.getCharCode().equals(codeValute))
                .map((valute)->CurrencyDto
                                .builder()
                                .currency(valute.getCharCode())
                                .rate(valute.getRate())
                                .build())
                .findFirst().get();

        var requestBuilder = get(url + "/rates/" + codeValute);
        //when
        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        //then
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(objectMapper.writeValueAsString(currencyDto)));

    }
//
    @ParameterizedTest
    @MethodSource("invalidCurrencyCodes")
    void getCurrencyRate_CurrencyDoesNotExists_ReturnsValidReposponseEntity(String invalidCode) throws Exception {
        //given
        var requestBuilder = get(url + "/rates/" + invalidCode);

        //when
        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON));

    }
    @ParameterizedTest
    @MethodSource("validConvertCurrencyDto")
    void convertCurrency_PayloadIsValid_ReturnsConvertCurrencyDto(ConvertCurrencyDto convertDto) throws Exception {
        int scaleConveration = 20;

        //given
        var requestBuilder = post(url+"/convert")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        new ConvertCurrencyRequest(convertDto.getFromCurrency(), convertDto.getToCurrency(), convertDto.getConvertedAmount())));
        var valute1 = valCurs.getValuteList().stream()
                .filter(valute -> valute.getCharCode().equals(convertDto.getFromCurrency()))
                .findFirst().get();
        var valute2 = valCurs.getValuteList().stream()
                .filter(valute -> valute.getCharCode().equals(convertDto.getToCurrency()))
                .findFirst().get();

        BigDecimal convertedAmount = convertDto
                .getConvertedAmount()
                .multiply(valute1.getRate())
                .divide(valute2.getRate(),scaleConveration, RoundingMode.HALF_UP);

        //when
        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        //then
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(objectMapper.writeValueAsString(ConvertCurrencyDto
                                .builder()
                                .fromCurrency(convertDto.getFromCurrency())
                                .toCurrency(convertDto.getToCurrency())
                                .convertedAmount(convertedAmount)
                                .build()
                        )));
    }
//
    @ParameterizedTest
    @MethodSource("unvalidConvertCurrencyDto")
    void convertCurrency_PayloadIsInValid_ReturnsValidResponseEntity(ConvertCurrencyDto convertDto) throws Exception {
        //given
        var requestBuilder = post(url + "/convert")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        new ConvertCurrencyRequest(convertDto.getFromCurrency(), convertDto.getToCurrency(), convertDto.getConvertedAmount())));
        //when
        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        //then
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON));

    }

    public static Stream<String> validCurrencyCodes() {
        int size = 10;
        return valCurs.getValuteList().stream().map(Valute::getCharCode).limit(size);

    }
    public static Stream<String> invalidCurrencyCodes() {
        return Stream.of("VAL","BAR","ZARU","-","x","y","very long code");

    }
    public static Stream<ConvertCurrencyDto> validConvertCurrencyDto() {

        var valuteList = valCurs.getValuteList();
        List<ConvertCurrencyDto> list = new ArrayList<>();

        int size = min(valuteList.size(),10);
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                    list.add(ConvertCurrencyDto
                            .builder()
                            .fromCurrency(valuteList.get(i).getCharCode())
                            .toCurrency(valuteList.get(j).getCharCode())
                            .convertedAmount(BigDecimal.valueOf(100))
                            .build());
            }
        }
        return list.stream();
    }
    public static Stream<ConvertCurrencyDto> unvalidConvertCurrencyDto() {
        var unvalidCodes = invalidCurrencyCodes().toList();
        List<ConvertCurrencyDto> list = new ArrayList<>();

        int size = min(unvalidCodes.size(),10);
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                list.add(ConvertCurrencyDto
                        .builder()
                        .fromCurrency(unvalidCodes.get(i))
                        .toCurrency(unvalidCodes.get(j))
                        .convertedAmount(BigDecimal.valueOf(new Random().nextDouble(-1000,1000)))
                        .build());
            }
        }
        return list.stream();
    }


}
