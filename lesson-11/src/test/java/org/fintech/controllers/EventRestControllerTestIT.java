package org.fintech.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.fintech.MainLesson11;
import org.fintech.controllers.payload.NewEventPayload;
import org.fintech.controllers.payload.UpdateEventPayload;
import org.fintech.dto.EventDto;
import org.fintech.store.repository.EventRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {MainLesson11.class})
@Testcontainers
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@Transactional
public class EventRestControllerTestIT {

    final String url = "/event";
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mvc;
    @Autowired
    EventRepository eventRepository;

    @Test
    @Sql({"/sql/insert_place.sql","/sql/insert_event.sql"})
    @DisplayName("Get events by filters - Should return list event dto when event exist")
    void getEventByFilter_EventExistsFilter1_ReturnListEvents() throws Exception {
        //given
        var requestBuilder = get(url)
                        .param("description","celebr")
                        .param("fromDate",String.valueOf(LocalDate.of(2024,10,16)))
                        .param("toDate", String.valueOf(LocalDate.of(2024,10,30)));

        //when
        mvc.perform(requestBuilder).andExpectAll(
                //then
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                content().json("""
                        [
                            {
                                "id": 9,
                                "name": "Moscow Another Day",
                                "description": "Another Some celebration of Moscow City",
                                "date": "2024-10-20",
                                "placeId": 2
                            },
                            {
                                "id": 10,
                                "name": "Novosibirsk Day",
                                "description": "Some celebration of Novosibirsk City",
                                "date": "2024-10-17",
                                "placeId": 6
                            },
                            {
                                "id": 11,
                                "name": "Sochi Day",
                                "description": "Some celebration of Sochi City",
                                "date": "2024-10-20",
                                "placeId": 3
                            }
                        ]
                        """));
    }
    @Test
    @Sql({"/sql/insert_place.sql","/sql/insert_event.sql"})
    @DisplayName("Get events by filters - Should return list event dto when event exist")
    void getEventByFilter_EventExistsFilter2_ReturnListEvents() throws Exception {
        //given
        var requestBuilder = get(url)
                .param("placeId","2")
                .param("fromDate",String.valueOf(LocalDate.of(2024,5,16)))
                .param("toDate", String.valueOf(LocalDate.of(2024,10,30)));

        //when
        mvc.perform(requestBuilder).andExpectAll(
                //then
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                content().json("""
                        [
                            {
                                "id": 8,
                                "name": "Moscow City Day",
                                "description": "Annual celebration of Moscow City",
                                "date": "2024-10-15",
                                "placeId": 2
                            },
                            {
                                "id": 9,
                                "name": "Moscow Another Day",
                                "description": "Another Some celebration of Moscow City",
                                "date": "2024-10-20",
                                "placeId": 2
                            }
                        ] 
                               """));
    }
    @Test
    @Sql({"/sql/insert_place.sql","/sql/insert_event.sql"})
    @DisplayName("Get events by filters - Should return empty list when events exist")
    void getEventByFilter_EventExistsFilter3_ReturnListEvents() throws Exception {
        //given
        var requestBuilder = get(url)
                .param("fromDate",String.valueOf(LocalDate.of(2022,5,16)))
                .param("toDate", String.valueOf(LocalDate.of(2023,10,30)));

        //when
        mvc.perform(requestBuilder).andExpectAll(
                //then
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                content().json("[]"));
    }
    @Test
    @Sql({"/sql/insert_place.sql","/sql/insert_event.sql"})
    @DisplayName("Get event by id - Should return EventDto when event exist")
    void getEventById_EventExists_ReturnEvent() throws Exception {
        //given
        int id = 5;
        var requestBuilder = get(url + "/" + id);
        //when
        mvc.perform(requestBuilder).andExpectAll(
                //then
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                content().json("""
                    {
                        "id": 5,
                        "name": "Yekaterinburg Art Fair",
                        "description": "An exhibition showcasing modern art.",
                        "date": "2024-09-15",
                        "placeId": 5
                    }
                    """));
    }
    @Test
    @DisplayName("Get event by id - Should return NOT FOUND when event doesnt exist")
    void getEventById_EventNotExists_ReturnNotFound() throws Exception {
        int id = 1;
        var requestBuilder = get(url + "/" + id);
        //when
        mvc.perform(requestBuilder).andExpectAll(
                //then
                status().isNotFound(),
                content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
    }

    @Test
    @Sql("/sql/insert_place.sql")
    @DisplayName("Create event - Should return EventDto when payload is valid")
    void createEvent_PayloadValid_ReturnEvent() throws Exception {
        //given
        var event = new NewEventPayload(1,"Some celebration", "One more test", LocalDate.of(2024,11,10));

        var requestBuilder = post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(event));
        //when
        var response = mvc.perform(requestBuilder)
                .andExpectAll(
                        //then
                        status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        var content = objectMapper.readValue(response.getContentAsString(), EventDto.class);

        assertThat(content.getId()).isEqualTo(1);
        assertThat(content.getName()).isEqualTo(event.name());
        assertThat(content.getDescription()).isEqualTo(event.description());
        assertThat(content.getDate()).isEqualTo(event.date());
    }
    @Test
    @DisplayName("Create event - Should return NOT FOUND when place does not exists")
    void createEvent_PlaceNotExist_ReturnNotFound() throws Exception {
        //given
        var event = new NewEventPayload(1,"Some celebration", "One more test", LocalDate.of(2024,11,10));

        var requestBuilder = post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(event));
        //when
        mvc.perform(requestBuilder)
                .andExpectAll(
                        //then
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
    }
    @ParameterizedTest
    @MethodSource("createEvent_PayloadInValid_ReturnBadRequest_methodSource")
    @DisplayName("Create event - Should return BAD REQUEST when payload is invalid")
    void createEvent_PayloadInValid_ReturnBadRequest(NewEventPayload event) throws Exception {
        //given
        var requestBuilder = post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(event));
        //when
        mvc.perform(requestBuilder)
                .andExpectAll(
                        //then
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
    }
    @Test
    @DisplayName("Update event - Should return ok when payload is valid")
    @Sql({"/sql/insert_place.sql","/sql/insert_event.sql"})
    void updateEvent_PayloadIsValid_ReturnsOk() throws Exception {
        //given
        int id = 1;
        var updateEvent = new UpdateEventPayload("Another-name", "Another-Description", LocalDate.of(2024,12,12));
        var requestBuilder = put(url+"/"+id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateEvent));
        //when
        mvc.perform(requestBuilder)
                .andExpectAll(
                        //then
                        status().isOk());
        var entity = eventRepository.findById(id).orElseThrow();
        assertThat(entity.getName()).isEqualTo(updateEvent.name());
        assertThat(entity.getDescription()).isEqualTo(updateEvent.description());
        assertThat(entity.getDate()).isEqualTo(updateEvent.date());
    }
    @Test
    @DisplayName("Update event - Should return NOT FOUND when payload is valid and event does not exists")
    void updateEvent_PayloadIsValidEventDoesNotExist_ReturnsNotFound() throws Exception {
        //given
        int id = 1;
        var updateEvent = new UpdateEventPayload("Another-name", "Another-Description", LocalDate.of(2024,12,12));
        var requestBuilder = put(url+"/"+id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateEvent));
        //when
        mvc.perform(requestBuilder)
                .andExpectAll(
                        //then
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
    }
    @Test
    @Sql({"/sql/insert_place.sql","/sql/insert_event.sql"})
    @DisplayName("Delete event - Should return no content when event exists")
    void deleteEvent_EventExists_ReturnsNoContent() throws Exception {
        //given
        int id = 1;
        var requestBuilder = delete(url+"/"+id);
        //when
        mvc.perform(requestBuilder).andExpectAll(
                //then
                status().isNoContent()
        );
        var entity = eventRepository.findById(id);
        assertThat(entity.isEmpty()).isTrue();
    }
    @Test
    @DisplayName("Delete entity - Should return NOT FOUND when entity doest exist")
    void deleteEntity_EntityDoesNotExist_ReturnsNotFound() throws Exception {
        //given
        int id = 1;
        var requestBuilder = delete(url+"/" + id);
        //when
        mvc.perform(requestBuilder).andExpectAll(
                //then
                status().isNotFound(),
                content().contentType(MediaType.APPLICATION_PROBLEM_JSON)
        );

    }
    static Stream<NewEventPayload> createEvent_PayloadInValid_ReturnBadRequest_methodSource() {
        return Stream.of(
                new NewEventPayload(null,null,null,null),
                new NewEventPayload(null,"some-name","some-description",LocalDate.now()),
                new NewEventPayload(1,null,"description",LocalDate.now()),
                new NewEventPayload(1,"some-name", "description",null),
                new NewEventPayload(1,"", "",LocalDate.now()));
    }


}
