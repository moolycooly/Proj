package org.fintech.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.fintech.Main;
import org.fintech.client.parser.PriceDeserializer;
import org.fintech.client.payload.EventResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wiremock.integrations.testcontainers.WireMockContainer;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {Main.class})
@Testcontainers
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class KudagoClientTest {

    @Container
    static WireMockContainer wireMockContainer = new WireMockContainer("wiremock/wiremock:2.35.0")
            .withMappingFromResource("events", KudagoClientTest.class, "events.json");
    @DynamicPropertySource
    static void wireMockProperties(DynamicPropertyRegistry registry) {
        registry.add("kudago.api.base-url", wireMockContainer::getBaseUrl);
    }
    @Autowired
    private KudagoClient kudagoClient;
    @Autowired
    private ObjectMapper objectMapper;

    private EventResponse eventResponse;
    @BeforeEach
    public void setup() throws JsonProcessingException {
        objectMapper.registerModule(new SimpleModule().addDeserializer(Integer.class,new PriceDeserializer()));
        eventResponse = objectMapper.readValue("""
                {
                      "count": 38,
                      "next": null,
                      "previous": null,
                      "results": [
                        {
                          "id": 208285,
                          "title": "новогоднее цирковое шоу «Вместе целая страна» в Новосибирском государственном цирке",
                          "description": "«Вместе целая страна» — это новогодний народный праздник на манеже цирка. Уникальное представление говорит со зрителями о дружбе, единстве народов нашей страны, многообразии культур и красоте природы.",
                          "price": "от 900 до 10 000 рублей, детям до 3 лет — бесплатно",
                          "is_free": false
                        },
                        {
                          "id": 208598,
                          "title": "8-й международный фестиваль трансформационных игр «Те самые игры. Сибирь»",
                          "description": "Мастера игр авторства Татьяны Мужицкой и Антона Нефёдова проводят фестиваль «Те самые игры. Сибирь», на котором каждый сможет погрузиться в пространство качественных игр и атмосферу любящих глаз. Примерить различные роли и открыть в себе что-то новое!",
                          "price": "от 7000 до 24 000 рублей",
                          "is_free": false
                        },
                        {
                          "id": 208539,
                          "title": "сольный концерт DS CREW",
                          "description": "Триумфаторы «Новых танцев на ТНТ», завоевавшие любовь миллионов зрителей, выступят с большой сольной программой в Новосибирске.",
                          "price": "от 2000 до 5500 руб.",
                          "is_free": false
                        }
                      ]
                    }
                
                
                """,EventResponse.class);
    }
    @Test
    void getEventsAsync_ArgumentsIsValid_Success() throws Exception {
        //given
        LocalDate dateFrom = LocalDate.parse("2023-10-21", DateTimeFormatter.ISO_DATE);
        LocalDate dateTo = LocalDate.parse("2024-10-25", DateTimeFormatter.ISO_DATE);
        //when
        var t = kudagoClient.getEventsAsync(dateFrom,dateTo);
        //then
        assertEquals(t,eventResponse.getResults());
    }
    @Test
    void getEventsAsync_ArgumentsIsInValid_ReturnsEmptyList() throws Exception {
        //given
        LocalDate dateFrom = LocalDate.parse("2026-10-21", DateTimeFormatter.ISO_DATE);
        LocalDate dateTo = LocalDate.parse("2027-10-25", DateTimeFormatter.ISO_DATE);
        //when
        var t = kudagoClient.getEventsAsync(dateFrom,dateTo);
        //then
        assertTrue(t.isEmpty());
    }


}
