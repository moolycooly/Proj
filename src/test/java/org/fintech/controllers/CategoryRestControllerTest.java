package org.fintech.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import org.fintech.dto.*;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure=false)
@ActiveProfiles("test")

public class CategoryRestControllerTest {
    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String url = "/api/v1/places/categories";

    @Test
    void getAllCategories_ReturnsValidResponseEntity_emptyContent() throws Exception {
         mockMvc.perform(get(url))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                          content().json("[]"));
    }
    @Test
    void getCategoryById_ReturnsValidResponseEntity_ContentNotEmpty() throws Exception {

        var category = createCategory(null,"test-slug","test-name");


        mockMvc.perform(get(url + "/" + category.getId()))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content()
                                .json(objectMapper.writeValueAsString(category)));
        deleteCategory(category.getId());
    }
    @Test
    void getCategoryById_ReturnsValidResponseEntity_Id_DoesNotExist() throws Exception {
        mockMvc.perform(get(url + "/1"))
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
    }


    @Test
    void createNewCategory_PayloadIsValid_ReturnsValidResponseEntity() throws Exception {

        var category = new CategoryDto(null,"test-slug", "test-name");


        var requestBuilder = post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(category));
        var response = mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse();
        var categoryDto1 =  objectMapper.readValue(response.getContentAsString(), CategoryDto.class);
        deleteCategory(categoryDto1.getId());
        assertEquals(categoryDto1.getSlug(),category.getSlug());
        assertEquals(category.getName(),category.getName());

    }
    @Test
    void createNewCategory_PayloadIsInvalid_ReturnsValidResponseEntity() throws Exception {
        // given

        var category = new CategoryDto(null, null, "test-slug");
        var requestBuilder = post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(category));
        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON));

    }
    @Test
    void deleteCategory_PayloadIsValid_ReturnsValidResponseEntity() throws Exception {
        var category = createCategory(null,"test-slug","test-name");
        deleteCategory(category.getId());
        mockMvc.perform(delete(url + "/" + category.getId()))
                .andExpectAll(
                        status().isNotFound());

    }
    @Test
    public void updateCategory_PayloadIsValid_ReturnsValidResponseEntity() throws Exception {
        var category = createCategory(null,"test-slug","test-name");

        var payload = new CategoryDto(null,"new-slug","new-name");

        mockMvc.perform(put(url + "/" + category.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpectAll(
                        status().isNoContent())
                .andReturn()
                .getResponse();
        var mvcResponse = mockMvc.perform(get(url + "/" + category.getId()))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON)
                ).andReturn().getResponse();

        var updatedCategory = objectMapper.readValue(mvcResponse.getContentAsString(), CategoryDto.class);
        assertEquals(updatedCategory.getId(),category.getId());
        assertEquals(updatedCategory.getSlug(),payload.getSlug());
        assertEquals(updatedCategory.getName(),payload.getName());
        deleteCategory(category.getId());
    }
    CategoryDto createCategory(Long id, String slug, String name) throws Exception {
        var categoryDto = new CategoryDto(id,slug,name);
        var requestBuilder = post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryDto));
        var response = mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        return objectMapper.readValue(response.getContentAsString(), CategoryDto.class);
    }
    void deleteCategory(Long id) throws Exception {
        mockMvc.perform(delete(url+"/"+id))
                .andExpect(status().isNoContent());
    }

}
