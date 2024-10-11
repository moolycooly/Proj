package org.fintech.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.fintech.Main;
import org.fintech.dto.CategoryDto;
import org.fintech.store.entity.CategoryEntity;
import org.fintech.store.repos.CategoryRepos;
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
public class CategoryRestControllerTestIT {
    @Container
    static WireMockContainer wireMockContainer = new WireMockContainer("wiremock/wiremock:2.35.0")
            .withMappingFromResource("categories", CategoryRestControllerTestIT.class, "categories.json");
    @DynamicPropertySource
    static void wireMockProperties(DynamicPropertyRegistry registry) {
        registry.add("kudago.api.base-url", wireMockContainer::getBaseUrl);
    }
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CategoryRepos categoryRepos;
    private final String url = "/api/v1/places/categories";

    @AfterEach
    void cleanUpEach() {
        categoryRepos.clear();
    }
    @BeforeEach
    void initEach() {
        categoryRepos.save(new CategoryEntity(1L, "test-slug", "test-name"));
    }
    @Test
    @Order(1)
    void getCategories_CategoriesExists_ReturnsListOfCategories() throws Exception {
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
                                       {
                                          "id": 1,
                                          "slug": "test-slug",
                                          "name": "test-name"
                                       },
                                       {
                                          "id": 123,
                                          "slug": "airports",
                                          "name": "Аэропорты"
                                        },
                                        {
                                          "id": 89,
                                          "slug": "amusement",
                                          "name": "Развлечения"
                                        },
                                        {
                                          "id": 114,
                                          "slug": "animal-shelters",
                                          "name": "Питомники"
                                        }
                                  ]
                                  """));
    }

    @Test
    void getCategory_CategoryExists_ReturnsCategory() throws Exception {
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
    void getCategory_CategoryDoesNotExist_ReturnsNotFound() throws Exception {
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
    void createCategory_RequestIsValid_ReturnsNewCategory() throws Exception {
        //given
        var category = new CategoryDto(2L,"test-slug", "test-name");
        var requestBuilder = post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(category));
        //when
        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        //then
                        status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(objectMapper.writeValueAsString(category)));
    }
    @ParameterizedTest
    @MethodSource("invalidRequestCategories")
    void createCategory_RequestIsInValid_ReturnsBadRequest(CategoryDto categoryDto) throws Exception {
        //given
        var requestBuilder = post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryDto));
        //when
        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        //then
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
    }
    @Test
    void updateCategory_RequestIsValid_ReturnsNoContent() throws Exception {
        //given
        Long id = 1L;
        var requestBuilder = put(url+"/"+id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CategoryDto(null,"updated-slug", "updated-name")));
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
    @MethodSource("invalidRequestCategories")
    void updateCategory_RequestIsInValid_ReturnsBadRequest(CategoryDto categoryDto) throws Exception {
        //given
        var requestBuilder = put(url+"/"+categoryDto.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryDto));
        //when
        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        //then
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                );

    }
    @Test
    void deleteCategory_CategoryExists_ReturnsNoContent() throws Exception {
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
    void deleteCategory_CategoryDoesNotExist_ReturnsNotFound() throws Exception {
        //given
        var requestBuilder = delete(url+"/2");
        //when
        mockMvc.perform(requestBuilder).andExpectAll(
                //then
                status().isNotFound(),
                content().contentType(MediaType.APPLICATION_PROBLEM_JSON)
        );

    }
    static Stream<CategoryDto> invalidRequestCategories() {
        return Stream.of(
                new CategoryDto(2L, null, "test-name"),
                new CategoryDto(2L, "", "test-name"),
                new CategoryDto(2L, "slug", null),
                new CategoryDto(2L, "slug", ""),
                new CategoryDto(2L, "s", "test-name"),
                new CategoryDto(2L, "s", "e"),
                new CategoryDto(2L, "slug", "v"),
                new CategoryDto(2L, "MoreThan50SymbolsTestSlugMoreThan50SymbolsTestSlugInvalid", "test-name"),
                new CategoryDto(2L, "test-slug", "MoreThan50SymbolsTestSlugMoreThan50SymbolsTestSlugInvalid")
        );
    }
}
