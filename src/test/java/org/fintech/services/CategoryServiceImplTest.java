package org.fintech.services;

import org.fintech.client.KudagoClient;
import org.fintech.dto.CategoryDto;
import org.fintech.services.impl.CategoryServiceImpl;
import org.fintech.store.entity.CategoryEntity;
import org.fintech.store.repos.CategoryRepos;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {
    @Mock
    private CategoryRepos categoryRepos;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private KudagoClient kudagoClient;

    @InjectMocks
    private CategoryServiceImpl categoryService;


    @Test
    void createCategory_ValidDto_ReturnsCreatedDto() {
        // given
        CategoryDto inputDto = new CategoryDto(null, "test-slug", "test-name");
        CategoryEntity mappedEntity = new CategoryEntity(null,"test-slug","test-name");
        CategoryEntity savedEntity = new CategoryEntity(1L,"test-slug","test-name");
        CategoryDto outputDto = new CategoryDto(1L, "test-slug", "test-name");

        when(modelMapper.map(inputDto, CategoryEntity.class)).thenReturn(mappedEntity);
        when(categoryRepos.save(mappedEntity)).thenReturn(savedEntity);
        when(modelMapper.map(savedEntity, CategoryDto.class)).thenReturn(outputDto);

        // when
        var result = categoryService.create(inputDto);

        // then
        assertNotNull(result);
        assertEquals(new CategoryDto(1L,"test-slug","test-name"),result);

        verify(modelMapper, times(1)).map(inputDto, CategoryEntity.class);
        verify(categoryRepos, times(1)).save(mappedEntity);
        verify(modelMapper, times(1)).map(savedEntity, CategoryDto.class);
        verifyNoMoreInteractions(modelMapper, categoryRepos);
    }

    @Test
    void findById_CategoryExists_ReturnsCategoryDto() {
        // given
        Long categoryId = 1L;
        CategoryEntity categoryEntity = new CategoryEntity(categoryId,"test-name","test-slug");
        CategoryDto categoryDto = new CategoryDto(categoryId, "test-name", "test-slug");
        when(categoryRepos.findById(categoryId)).thenReturn(Optional.of(categoryEntity));
        when(modelMapper.map(categoryEntity, CategoryDto.class)).thenReturn(categoryDto);

        // when
        Optional<CategoryDto> result = categoryService.findById(categoryId);

        // then
        assertTrue(result.isPresent());
        assertEquals(categoryDto, result.get());
        verify(categoryRepos, times(1)).findById(categoryId);
        verify(modelMapper, times(1)).map(categoryEntity, CategoryDto.class);
        verifyNoMoreInteractions(categoryRepos, modelMapper);
    }

    @Test
    void findById_CategoryDoesNotExist_ReturnsEmptyOptional() {
        // given
        Long categoryId = 1L;
        when(categoryRepos.findById(categoryId)).thenReturn(Optional.empty());

        // when
        Optional<CategoryDto> result = categoryService.findById(categoryId);

        // then
        assertFalse(result.isPresent());

        verify(categoryRepos, times(1)).findById(categoryId);
        verifyNoMoreInteractions(categoryRepos);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void updateCategory_CategoryExists_UpdatesSuccessfully() {
        // given
        Long categoryId = 1L;
        CategoryDto categoryDto = new CategoryDto(null, "test-slug", "test-name");
        CategoryEntity categoryEntity = new CategoryEntity(categoryId,"test-slug","test-name");
        CategoryEntity updatedEntity = new CategoryEntity(categoryId,"new-slug","new-name");

        when(categoryRepos.findById(categoryId)).thenReturn(Optional.of(categoryEntity));
        when(modelMapper.map(categoryDto, CategoryEntity.class)).thenReturn(updatedEntity);

        // when
        categoryService.update(categoryId, categoryDto);
        // then
        verify(categoryRepos, times(1)).findById(categoryId);
        verify(modelMapper, times(1)).map(categoryDto, CategoryEntity.class);
        verify(categoryRepos, times(1)).update(categoryId, updatedEntity);
        verifyNoMoreInteractions(categoryRepos, modelMapper);
    }

    @Test
    void updateCategory_CategoryDoesNotExist_ThrowsNoSuchElementException() {
        // given
        Long categoryId = 1L;
        CategoryDto updateCategoryDto = new CategoryDto(null, "updated-slug", "updated-name");

        when(categoryRepos.findById(categoryId)).thenReturn(Optional.empty());

        //when
        assertThrows(NoSuchElementException.class, () ->
            categoryService.update(categoryId, updateCategoryDto));

        verify(categoryRepos, times(1)).findById(categoryId);
        verifyNoMoreInteractions(categoryRepos);
        verifyNoInteractions(modelMapper);
    }


    @Test
    void deleteById_CategoryExists_DeleteSuccessfully() {
        // given
        Long categoryId = 1L;
        CategoryEntity existingEntity = new CategoryEntity(1L,"test-slug","test-name");
        existingEntity.setId(categoryId);

        when(categoryRepos.findById(categoryId)).thenReturn(Optional.of(existingEntity));
        doNothing().when(categoryRepos).deleteById(categoryId);

        // when
        categoryService.deleteById(categoryId);

        // then
        verify(categoryRepos, times(1)).findById(categoryId);
        verify(categoryRepos, times(1)).deleteById(categoryId);
        verifyNoMoreInteractions(categoryRepos);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void deleteById_CategoryDoesNotExist_ThrowsNoSuchElementException() {
        // given
        Long categoryId = 1L;
        when(categoryRepos.findById(categoryId)).thenReturn(Optional.empty());

        // when
        assertThrows(NoSuchElementException.class, () -> {
            categoryService.deleteById(categoryId);
        });

        // then
        verify(categoryRepos, times(1)).findById(categoryId);
        verifyNoMoreInteractions(categoryRepos);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void findAll_NoCategoriesExist_ReturnsEmptyList() {
        // given
        when(categoryRepos.findAll()).thenReturn(List.of());

        // when
        var result = categoryService.findAll();

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(categoryRepos, times(1)).findAll();
        verifyNoMoreInteractions(categoryRepos);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void findAll_CategoriesExist_ReturnsListOfCategoryDtos() {
        // given
        CategoryEntity categoryEntity1 = new CategoryEntity(1L,"first-slug","first-name");
        CategoryEntity categoryEntity2 = new CategoryEntity(2L,"second-slug","second-name");

        CategoryDto categoryDto1 = new CategoryDto(1L,"first-slug","first-name");
        CategoryDto categoryDto2 = new CategoryDto(2L,"second-slug","second-name");

        when(categoryRepos.findAll()).thenReturn(List.of(categoryEntity1, categoryEntity2));
        when(modelMapper.map(categoryEntity1, CategoryDto.class)).thenReturn(categoryDto1);
        when(modelMapper.map(categoryEntity2, CategoryDto.class)).thenReturn(categoryDto2);

        // when
        var result = categoryService.findAll();

        // then
        assertNotNull(result);
        assertEquals(List.of(categoryDto1, categoryDto2), result);

        verify(categoryRepos, times(1)).findAll();
        verify(modelMapper, times(1)).map(categoryEntity1, CategoryDto.class);
        verify(modelMapper, times(1)).map(categoryEntity2, CategoryDto.class);
        verifyNoMoreInteractions(categoryRepos, modelMapper);
    }

}
