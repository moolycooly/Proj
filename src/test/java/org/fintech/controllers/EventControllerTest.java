package org.fintech.controllers;

import org.fintech.Main;
import org.fintech.services.EventService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {Main.class})
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class EventControllerTest {

    private final String url = "/api/v1/events";
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;


    @Test
    public void getEvents_EventsExistsPayloadIsValid_ReturnListOfEventDto() throws Exception {
        //given
        var sourseCurencyCode = "USD";
        var requestBuilder = get(url + "?budget=100&currency="+sourseCurencyCode+"&dateFrom=2023-10-21&dateTo=2024-10-25");
        when(eventService.getEventsCompletableFuture(anyDouble(),eq("USD"),any(),any()))
                .thenReturn(CompletableFuture.completedFuture(List.of()));



        //when
        mockMvc.perform(requestBuilder)
                .andExpectAll(//then
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("[]")
                );

    }



}
