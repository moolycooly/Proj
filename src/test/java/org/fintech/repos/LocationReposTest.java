package org.fintech.repos;

import org.fintech.store.entity.CategoryEntity;
import org.fintech.store.entity.LocationEntity;
import org.fintech.store.repos.LocationRepos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LocationReposTest {
    private LocationRepos locationRepos;

    @BeforeEach
    public void setUp() {
        locationRepos = new LocationRepos();
    }
    @Test
    void save_idIsNull_ReturnValidId() {
        //given
        var location = new LocationEntity(null,"test-name","test-slug");

        //when
        var result = locationRepos.save(location);

        //then
        assertEquals(result,new LocationEntity(1L,"test-name","test-slug"));
    }
    @Test
    void save_idIsAlreadyExist_ReturnValidId() {
        //given
        LocationEntity prepareLocation = new LocationEntity(1L,"test-name","test-slug");
        locationRepos.save(prepareLocation);
        var location = new LocationEntity(1L,"test-name","test-slug");
        //when
        var result = locationRepos.save(location);
        //then
        assertEquals(result,new LocationEntity(2L,"test-name","test-slug"));
    }

    @Test
    void findById_IdExists_ReturnValidEntity() {
        //given
        var location = new LocationEntity(1L,"test-name","test-slug");
        locationRepos.save(location);
        //when
        var result = locationRepos.findById(1L);
        //then
        assertTrue(result.isPresent());
        assertEquals(result.get(),new LocationEntity(1L,"test-name","test-slug"));
    }
    @Test
    void findById_IdDoesNotExist_ReturnEmptyOptional() {
        //given

        //when
        var result = locationRepos.findById(1L);
        //then
        assertFalse(result.isPresent());
    }
    @Test
    void findAll_EntitiesAreEmpty_ReturnEmptyList() {
        //given

        //when
        var result = locationRepos.findAll();
        //then
        assertTrue(result.isEmpty());
    }
    @Test
    void findAll_EntitiesExists_ReturnListOfEntities() {
        //given
        var location1 = new LocationEntity(1L,"test-name","test-slug");
        var location2 = new LocationEntity(2L,"test-name","test-slug");
        locationRepos.save(location1);
        locationRepos.save(location2);
        //when
        var result = locationRepos.findAll();
        //then
        assertEquals(result, List.of(location1, location2));
    }
    @Test
    void deleteById_IdIsNull_ReturnNoSuchElementException() {
        //given
        Long id = null;
        //when
        assertThrows(NullPointerException.class,()-> locationRepos.deleteById(id));
    }
    @Test
    void deleteById_IdExist_Successfully() {
        //given
        var location = new LocationEntity(1L,"test-name","test-slug");
        locationRepos.save(location);
        //when
        locationRepos.deleteById(1L);
        //then
        var result = locationRepos.findById(1L);
        assertFalse(result.isPresent());
    }
    @Test
    void update_IdDoesIsNull_ReturnNoSuchElementException() {
        Long id = null;
        var updatedLocation = new LocationEntity(1L,"new-slug","new-name");
        //when
        assertThrows(NullPointerException.class,()-> locationRepos.update(id, updatedLocation));
    }
    @Test
    void update_IdExist_Successfully() {
        Long id = 1L;
        var location = new LocationEntity(1L,"test-slug","test-name");
        locationRepos.save(location);
        var updatedLocation = new LocationEntity(1L,"new-slug","new-name");
        //when
        locationRepos.update(id,updatedLocation);
        //then
        var result = locationRepos.findById(1L);
        assertTrue(result.isPresent());
        assertEquals(result.get(),updatedLocation);
    }
    @Test
    void clear_successfully() {
        //given
        for(int i = 0; i < 10; i++) {
            locationRepos.save(new LocationEntity(null,"test-name","test-slug"));
        }
        //when
        locationRepos.clear();
        var result = locationRepos.findAll();
        //then
        assertTrue(result.isEmpty());

    }
}
