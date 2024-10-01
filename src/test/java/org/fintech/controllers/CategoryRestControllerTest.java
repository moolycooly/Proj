package org.fintech.controllers;

import org.fintech.services.CategoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.fintech.dto.CategoryDto;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryRestControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryRestController categoryRestController;

    @Test
    void getCategories_CategoriesDoNotExist_ReturnsEmptyList() {
        //given
        when(categoryService.findAll()).thenReturn(List.of());

        //when
        var result = categoryRestController.getCategories();

        //then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(categoryService, times(1)).findAll();
        verifyNoMoreInteractions(categoryService);
    }

    @Test
    void getCategories_CategoriesExist_ReturnsListOfCategories() {
        //given
        var categoryDto = new CategoryDto(1L, "test-slug", "test-name");
        when(categoryService.findAll()).thenReturn(List.of(categoryDto));

        //when
        var result = categoryRestController.getCategories();

        //then
        assertNotNull(result);
        assertEquals(List.of(categoryDto), result);

        verify(categoryService, times(1)).findAll();
        verifyNoMoreInteractions(categoryService);
    }

    @Test
    void getCategory_CategoryDoesNotExist_ReturnsNotFound() {
        //given
        Long categoryId = 1L;
        when(categoryService.findById(categoryId)).thenReturn(Optional.empty());

        //when
        var exception = assertThrows(NoSuchElementException.class,
                () -> categoryRestController.getCategory(categoryId));
        //then
        assertEquals(exception.getMessage(),"Category with id: 1 not found");

        verify(categoryService, times(1)).findById(categoryId);
        verifyNoMoreInteractions(categoryService);
    }

    @Test
    void getCategory_CategoryExists_ReturnsCategory() {
        //given
        Long categoryId = 1L;
        var categoryDto = new CategoryDto(categoryId, "test-slug", "Test Category");
        when(categoryService.findById(categoryId)).thenReturn(Optional.of(categoryDto));

        //when
        var result = categoryRestController.getCategory(categoryId);

        //then
        assertNotNull(result);
        assertEquals(result, categoryDto);

        verify(categoryService, times(1)).findById(categoryId);
        verifyNoMoreInteractions(categoryService);
    }

    @Test
    void createCategory_RequestIsValid_ReturnsNewCategory() {
        //given
        var categoryDto = new CategoryDto(1L, "test-slug", "test-name");
        when(categoryService.create(categoryDto)).thenReturn(categoryDto);

        //when
        var response = categoryRestController.createCategory(categoryDto);

        //then
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(categoryDto, response.getBody());

        verify(categoryService, times(1)).create(categoryDto);
        verifyNoMoreInteractions(categoryService);
    }

    @Test
    void updateCategory_RequestIsValid_ReturnsNoContent() {
        //given
        Long categoryId = 1L;
        var categoryDto = new CategoryDto(null, "test-slug", "test-name");
        doNothing().when(categoryService).update(categoryId,categoryDto);

        //when
        var response = categoryRestController.updateCategory(categoryId, categoryDto);

        //then
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        verify(categoryService, times(1)).update(categoryId,categoryDto);
        verifyNoMoreInteractions(categoryService);
    }

    @Test
    void updateCategory_CategoryDoesNotExist_ThrowsNoSuchElementException() {
        //given
        Long categoryId = 1L;
        var categoryDto = new CategoryDto(null, "test-slug", "test-name");

        doThrow(new NoSuchElementException("Category with id: " + categoryId + " not found"))
                .when(categoryService).update(categoryId,categoryDto);

        //when
        var exception = assertThrows(NoSuchElementException.class, () ->
            categoryRestController.updateCategory(categoryId, categoryDto));

        //then
        assertEquals("Category with id: 1 not found", exception.getMessage());
        verify(categoryService, times(1)).update(categoryId,categoryDto);
        verifyNoMoreInteractions(categoryService);
    }
    @Test
    void deleteCategory_CategoryExists_ReturnsNoContent() {
        //given
        Long categoryId = 1L;
        doNothing().when(categoryService).deleteById(categoryId);

        //when
        var response = categoryRestController.deleteCategory(categoryId);

        //then
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        verify(categoryService, times(1)).deleteById(categoryId);
        verifyNoMoreInteractions(categoryService);
    }
    @Test
    void deleteCategory_CategoryDoesNotExist_ThrowsNoSuchElementException() {
        //given
        Long categoryId = 1L;
        doThrow(new NoSuchElementException("Category with id: " + categoryId + " not found"))
                .when(categoryService).deleteById(categoryId);

        //when
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
            categoryRestController.deleteCategory(categoryId)
        );
        //then
        assertEquals("Category with id: 1 not found", exception.getMessage());

        verify(categoryService, times(1)).deleteById(categoryId);
        verifyNoMoreInteractions(categoryService);
    }



}