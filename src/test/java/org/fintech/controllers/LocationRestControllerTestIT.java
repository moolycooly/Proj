package org.fintech.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.fintech.Main;
import org.fintech.dto.LocationDto;
import org.fintech.store.entity.LocationEntity;
import org.fintech.store.repos.LocationRepos;
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

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {Main.class})
@Testcontainers
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LocationRestControllerTestIT {
    @Container
    static WireMockContainer wireMockContainer = new WireMockContainer("wiremock/wiremock:2.35.0")
            .withMappingFromResource("locations", LocationRestControllerTestIT.class, "locations.json");
    @DynamicPropertySource
    static void wireMockProperties(DynamicPropertyRegistry registry) {
        registry.add("kudago.api.base-url", wireMockContainer::getBaseUrl);
    }
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private LocationRepos locationRepos;
    private final String url = "/api/v1/locations";

    @AfterEach
    void cleanUpEach() {
        locationRepos.clear();
    }
    @BeforeEach
    void initEach() {
        locationRepos.save(new LocationEntity(1L, "test-slug", "test-name"));
    }
    @Test
    @Order(1)
    void getLocations_LocationsAreFromKudagoMock_ReturnsListOfLocations() throws Exception {
        //given
        var requestBuilder = get(url);
        //when
        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        //then
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                  [       
                                      {   "id": 1,
                                          "slug": "ekb",
                                          "name": "Екатеринбург"
                                      },
                                      {   "id": 2,
                                          "slug": "kzn",
                                          "name": "Казань"
                                      },
                                      {   "id": 3,
                                          "slug": "msk",
                                          "name": "Москва"
                                      },
                                      {   "id": 4,
                                          "slug": "nnv",
                                          "name": "Нижний Новгород"
                                      },
                                      {   "id": 5,
                                          "slug": "spb",
                                          "name": "Санкт-Петербург"
                                      },
                                      {   "id": 6,
                                          "slug": "test-slug",
                                          "name": "test-name"
                                      }      
                                  ]
                                  """));
    }

    @Test
    void getLocation_LocationExists_ReturnsLocation() throws Exception {
        //given
        var requestBuilder = get(url+"/1");

        //when
        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        //then
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                        {
                                          "id": 1,
                                          "slug": "test-slug",
                                          "name": "test-name"
                                        }
                          """));
    }
    @Test
    void getLocation_LocationDoesNotExist_ReturnsNotFound() throws Exception {
        //given
        var requestBuilder = get(url+"/2");

        //when
        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        //then
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
    }
    @Test
    void createLocation_RequestIsValid_ReturnsNewLocation() throws Exception {
        //given
        var locationDto = new LocationDto(2L,"test-slug", "test-name");
        var requestBuilder = post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(locationDto));
        //when
        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        //then
                        status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(objectMapper.writeValueAsString(locationDto)));
    }
    @ParameterizedTest
    @MethodSource("invalidRequestLocations")
    void createLocation_RequestIsInValid_ReturnsBadRequest(LocationDto locationDto) throws Exception {
        //given
        var requestBuilder = post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(locationDto));
        //when
        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        //then
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
    }
    @Test
    void updateLocation_RequestIsValid_ReturnsNoContent() throws Exception {
        //given
        Long id = 1L;
        var requestBuilder = put(url+"/"+id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LocationDto(null,"updated-slug", "updated-name")));
        //when
        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        //then
                        status().isNoContent());
        mockMvc.perform(get(url+"/"+id))
                .andExpectAll(
                    status().isOk(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    content().json("""
                                            {
                                              "id": 1,
                                              "slug": "updated-slug",
                                              "name": "updated-name"
                                            }
                              """));

    }
    @ParameterizedTest
    @MethodSource("invalidRequestLocations")
    void updateLocation_RequestIsInValid_ReturnsBadRequest(LocationDto locationDto) throws Exception {
        //given
        var requestBuilder = put(url+"/"+locationDto.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(locationDto));
        //when
        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        //then
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                );
    }
    @Test
    void deleteLocation_LocationExists_ReturnsNoContent() throws Exception {
        //given
        var requestBuilder = delete(url+"/1");
        //when
        mockMvc.perform(requestBuilder).andExpectAll(
                //then
                status().isNoContent()
        );
        mockMvc.perform(get(url+"/1"))
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
    }
    @Test
    void deleteLocation_LocationDoesNotExist_ReturnsNotFound() throws Exception {
        //given
        var requestBuilder = delete(url+"/2");
        //when
        mockMvc.perform(requestBuilder).andExpectAll(
                //then
                status().isNotFound(),
                content().contentType(MediaType.APPLICATION_PROBLEM_JSON)
        );

    }
    static Stream<LocationDto> invalidRequestLocations() {
        return Stream.of(
                new LocationDto(2L, null, "test-name"),
                new LocationDto(2L, "", "test-name"),
                new LocationDto(2L, "slug", null),
                new LocationDto(2L, "slug", ""),
                new LocationDto(2L, "s", "test-name"),
                new LocationDto(2L, "s", "e"),
                new LocationDto(2L, "slug", "v"),
                new LocationDto(2L, "MoreThan50SymbolsTestSlugMoreThan50SymbolsTestSlugInvalid", "test-name"),
                new LocationDto(2L, "test-slug", "MoreThan50SymbolsTestSlugMoreThan50SymbolsTestSlugInvalid")
        );
    }
}
