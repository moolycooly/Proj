package org.fintech.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.fintech.dto.LocationDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure=false)
@ActiveProfiles("test")

public class LocationRestControllerTest {
    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String url = "/api/v1/locations";


    @Test
    void getAllLocations_ReturnsValidResponseEntity_emptyContent() throws Exception {
        mockMvc.perform(get(url))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("[]"));
    }
    @Test
    void getLocationById_ReturnsValidResponseEntity_ContentNotEmpty() throws Exception {

        var location = createLocation(null,"test-slug","test-name");


        mockMvc.perform(get(url + "/" + location.getId()))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content()
                                .json(objectMapper.writeValueAsString(location)));
        deleteLocation(location.getId());
    }
    @Test
    void getLocationById_ReturnsValidResponseEntity_Id_DoesNotExist() throws Exception {
        mockMvc.perform(get(url + "/1"))
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
    }


    @Test
    void createNewLocation_PayloadIsValid_ReturnsValidResponseEntity() throws Exception {

        var location = new LocationDto(null,"test-slug", "test-name");


        var requestBuilder = post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(location));
        var response = mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        var location1 =  objectMapper.readValue(response.getContentAsString(), LocationDto.class);
        deleteLocation(location1.getId());
        assertEquals(location1.getSlug(),location.getSlug());
        assertEquals(location.getName(),location.getName());

    }
    @Test
    void createNewLocation_PayloadIsInvalid_ReturnsValidResponseEntity() throws Exception {
        // given

        var location = new LocationDto(null, null, "test-slug");
        var requestBuilder = post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(location));
        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON));

    }
    @Test
    void deleteLocation_PayloadIsValid_ReturnsValidResponseEntity() throws Exception {
        var location = createLocation(null,"test-slug","test-name");
        deleteLocation(location.getId());
        mockMvc.perform(delete(url + "/" + location.getId()))
                .andExpectAll(
                        status().isNotFound());

    }
    @Test
    public void updateLocation_PayloadIsValid_ReturnsValidResponseEntity() throws Exception {
        var location = createLocation(null,"test-slug","test-name");

        var payload = new LocationDto(null,"new-slug","new-name");

        mockMvc.perform(put(url + "/" + location.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpectAll(
                        status().isNoContent())
                .andReturn()
                .getResponse();
        var mvcResponse = mockMvc.perform(get(url + "/" + location.getId()))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON)
                ).andReturn().getResponse();

        var updatedLocation = objectMapper.readValue(mvcResponse.getContentAsString(), LocationDto.class);
        assertEquals(updatedLocation.getId(),location.getId());
        assertEquals(updatedLocation.getSlug(),payload.getSlug());
        assertEquals(updatedLocation.getName(),payload.getName());
        deleteLocation(location.getId());
    }
    LocationDto createLocation(Long id, String slug, String name) throws Exception {
        var location = new LocationDto(id,slug,name);
        var requestBuilder = post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(location));
        var response = mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        return objectMapper.readValue(response.getContentAsString(), LocationDto.class);
    }
    void deleteLocation(Long id) throws Exception {
        mockMvc.perform(delete(url+"/"+id))
                .andExpect(status().isNoContent());
    }

}
