package org.fintech.repos;

import org.fintech.store.entity.CategoryEntity;
import org.fintech.store.repos.CategoryRepos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CategoryReposTest {

    private CategoryRepos categoryRepos;

    @BeforeEach
    public void setUp() {
        categoryRepos = new CategoryRepos();
    }
    @Test
    void save_idIsNull_ReturnValidId() {
        //given
        var category = new CategoryEntity(null,"test-name","test-slug");

        //when
        var result = categoryRepos.save(category);

        //then
        assertEquals(result,new CategoryEntity(1L,"test-name","test-slug"));
    }
    @Test
    void save_idIsAlreadyExist_ReturnValidId() {
        //given
        CategoryEntity prepareCategory = new CategoryEntity(1L,"test-name","test-slug");
        categoryRepos.save(prepareCategory);
        var category = new CategoryEntity(1L,"test-name","test-slug");
        //when
        var result = categoryRepos.save(category);
        //then
        assertEquals(result,new CategoryEntity(2L,"test-name","test-slug"));
    }

    @Test
    void findById_IdExists_ReturnValidEntity() {
        //given
        var category = new CategoryEntity(1L,"test-name","test-slug");
        categoryRepos.save(category);
        //when
        var result = categoryRepos.findById(1L);
        //then
        assertTrue(result.isPresent());
        assertEquals(result.get(),new CategoryEntity(1L,"test-name","test-slug"));
    }
    @Test
    void findById_IdDoesNotExist_ReturnEmptyOptional() {
        //given

        //when
        var result = categoryRepos.findById(1L);
        //then
        assertFalse(result.isPresent());
    }
    @Test
    void findAll_EntitiesAreEmpty_ReturnEmptyList() {
        //given

        //when
        var result = categoryRepos.findAll();
        //then
        assertTrue(result.isEmpty());
    }
    @Test
    void findAll_EntitiesExists_ReturnListOfEntities() {
        //given
        var category1 = new CategoryEntity(1L,"test-name","test-slug");
        var category2 = new CategoryEntity(2L,"test-name","test-slug");
        categoryRepos.save(category1);
        categoryRepos.save(category2);
        //when
        var result = categoryRepos.findAll();
        //then
        assertEquals(result, List.of(category1,category2));
    }
    @Test
    void deleteById_IdIsNull_ReturnNoSuchElementException() {
        //given
        Long id = null;
        //when
        assertThrows(NullPointerException.class,()->categoryRepos.deleteById(id));
    }
    @Test
    void deleteById_IdExist_Successfully() {
        //given
        var category = new CategoryEntity(1L,"test-name","test-slug");
        categoryRepos.save(category);
        //when
        categoryRepos.deleteById(1L);
        //then
        var result = categoryRepos.findById(1L);
        assertFalse(result.isPresent());
    }
    @Test
    void update_IdDoesIsNull_ReturnNoSuchElementException() {
        Long id = null;
        var updatedCategory = new CategoryEntity(1L,"new-slug","new-name");
        //when
        assertThrows(NullPointerException.class,()->categoryRepos.update(id,updatedCategory));
    }
    @Test
    void update_IdExist_Successfully() {
        //given
        Long id = 1L;
        var category = new CategoryEntity(1L,"test-slug","test-name");
        categoryRepos.save(category);
        var updatedCategory = new CategoryEntity(1L,"new-slug","new-name");
        //when
        categoryRepos.update(id,updatedCategory);
        //then
        var result = categoryRepos.findById(1L);
        assertTrue(result.isPresent());
        assertEquals(result.get(),updatedCategory);
    }
    @Test
    void clear_successfully() {
        //given
        for(int i = 0; i < 10; i++) {
            categoryRepos.save(new CategoryEntity(null,"test-name","test-slug"));
        }
        //when
        categoryRepos.clear();
        var result = categoryRepos.findAll();
        //then
        assertTrue(result.isEmpty());

    }
}
