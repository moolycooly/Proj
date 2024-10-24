package org.fintech.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.fintech.MainLesson9;
import org.fintech.controllers.payload.NewPlacePayload;
import org.fintech.controllers.payload.UpdatePlacePayload;
import org.fintech.dto.PlaceDto;
import org.fintech.store.repository.PlaceRepository;
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

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {MainLesson9.class})
@Testcontainers
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@Transactional
public class PlaceRestControllerTestIT {

    final String url = "/place";
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mvc;
    @Autowired
    PlaceRepository placeRepository;


    @Test
    @Sql("/sql/insert_place.sql")
    @DisplayName("Get all places - Should return list of PlaceDto when places exist")
    void getAllPlaces_PlaceExists_ReturnListPlaces() throws Exception {
        //given
        int id = 5;
        var requestBuilder = get(url);
        //when
        mvc.perform(requestBuilder).andExpectAll(
                //then
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                content().json("""
                        [
                            {
                                "id": 1,
                                "slug": "Saint Petersburg",
                                "name": "Summer Garden"
                            },
                            {
                                "id": 2,
                                "slug": "Moscow",
                                "name": "Red Square"
                            },
                            {
                                "id": 3,
                                "slug": "Sochi",
                                "name": "Rosa Khutor"
                            },
                            {
                                "id": 4,
                                "slug": "Kazan",
                                "name": "Kremlin"
                            },
                            {
                                "id": 5,
                                "slug": "Yekaterinburg",
                                "name": "Church on the Blood"
                            },
                            {
                                "id": 6,
                                "slug": "Novosibirsk",
                                "name": "Novosibirsk Opera and Ballet Theatre"
                            },
                            {
                                "id": 7,
                                "slug": "Vladivostok",
                                "name": "Russian Island Bridge"
                            }
                        ]"""));
    }
    @Test
    @Sql("/sql/insert_place.sql")
    @DisplayName("Get place by id - Should return PlaceDto when place exist")
    void getPlaceById_PlaceExists_ReturnPlace() throws Exception {
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
                        "slug":"Yekaterinburg",
                        "name":"Church on the Blood"
                    }
                    """));
    }
    @Test
    @DisplayName("Get place by id - Should return NOT FOUND when place doesnt exist")
    void getPlaceById_PlaceNotExists_ReturnNotFound() throws Exception {
        int id = 1;
        var requestBuilder = get(url + "/" + id);
        //when
        mvc.perform(requestBuilder).andExpectAll(
                //then
                status().isNotFound(),
                content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
    }

    @Test
    @DisplayName("Create place - Should return PlaceDto when payload is valid")
    void createPlace_PayloadValid_ReturnPlaceDto() throws Exception {
        //given
        var place = new NewPlacePayload("Some-slug", "Some-name");
        var requestBuilder = post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(place));
        //when
        var response = mvc.perform(requestBuilder)
                .andExpectAll(
                        //then
                        status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        var content = objectMapper.readValue(response.getContentAsString(), PlaceDto.class);

        assertThat(content.getId()).isEqualTo(1);
        assertThat(content.getName()).isEqualTo(place.name());
        assertThat(content.getSlug()).isEqualTo(place.slug());
    }
    @ParameterizedTest
    @MethodSource("createPlace_PayloadInValid_ReturnBadRequest_methodSource")
    @DisplayName("Create place - Should return BAD REQUEST when payload is invalid")
    void createPlace_PayloadInValid_ReturnBadRequest(NewPlacePayload place) throws Exception {
        //given
        var requestBuilder = post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(place));
        //when
        mvc.perform(requestBuilder)
                .andExpectAll(
                        //then
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
    }
    @Test
    @DisplayName("Update place - Should return ok when payload is valid")
    @Sql("/sql/insert_place.sql")
    void updatePlace_PayloadIsValid_ReturnsOk() throws Exception {
        //given
        int id = 1;
        var updatePlace = new UpdatePlacePayload("Another-slug","Another-name");
        var requestBuilder = put(url+"/"+id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatePlace));
        //when
        mvc.perform(requestBuilder)
                .andExpectAll(
                        //then
                        status().isOk());
        var entity = placeRepository.findById(id).orElseThrow();
        assertThat(entity.getName()).isEqualTo(updatePlace.name());
        assertThat(entity.getSlug()).isEqualTo(updatePlace.slug());
    }
    @Test
    @DisplayName("Update place - Should return NOT FOUND when place is valid and place does not exists")
    void updatePlace_PayloadIsValidPlaceDoesNotExist_ReturnsNotFound() throws Exception {
        //given
        int id = 1;
        var updatePlace = new UpdatePlacePayload("Another-slug","Another-name");
        var requestBuilder = put(url+"/"+id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatePlace));
        //when
        mvc.perform(requestBuilder)
                .andExpectAll(
                        //then
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
    }
    @Test
    @Sql("/sql/insert_place.sql")
    @DisplayName("Delete place - Should return no content when place exists")
    void deletePlace_PlaceExists_ReturnsNoContent() throws Exception {
        //given
        int id = 1;
        var requestBuilder = delete(url+"/"+id);
        //when
        mvc.perform(requestBuilder).andExpectAll(
                //then
                status().isNoContent()
        );
        var entity = placeRepository.findById(id);
        assertThat(entity.isEmpty()).isTrue();
    }
    @Test
    @DisplayName("Delete place - Should return NOT FOUND when place doest exist")
    void deletePlace_PlaceDoesNotExist_ReturnsPlaceNotFound() throws Exception {
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
    static Stream<NewPlacePayload> createPlace_PayloadInValid_ReturnBadRequest_methodSource() {
        return Stream.of(
                new NewPlacePayload(null,null),
                new NewPlacePayload(null,"some-name"),
                new NewPlacePayload("some-slug", null),
                new NewPlacePayload("some-slug",""),
                new NewPlacePayload("","some-name"));
    }

}
