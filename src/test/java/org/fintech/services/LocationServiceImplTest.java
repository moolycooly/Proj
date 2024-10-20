package org.fintech.services;

import org.fintech.client.KudagoClient;
import org.fintech.dto.LocationDto;
import org.fintech.services.impl.LocationServiceImpl;
import org.fintech.store.entity.LocationEntity;
import org.fintech.store.repos.LocationRepos;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LocationServiceImplTest {
    @Mock
    private LocationRepos locationRepos;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private KudagoClient kudagoClient;

    @InjectMocks
    private LocationServiceImpl locationService;


    @Test
    void createLocation_ValidDto_ReturnsCreatedDto() {
        // given
        LocationDto inputDto = new LocationDto(null, "test-slug", "test-name");
        LocationEntity mappedEntity = new LocationEntity(null,"test-slug","test-name");
        LocationEntity savedEntity = new LocationEntity(1L,"test-slug","test-name");
        LocationDto outputDto = new LocationDto(1L, "test-slug", "test-name");

        when(modelMapper.map(inputDto, LocationEntity.class)).thenReturn(mappedEntity);
        when(locationRepos.save(mappedEntity)).thenReturn(savedEntity);
        when(modelMapper.map(savedEntity, LocationDto.class)).thenReturn(outputDto);

        // when
        var result = locationService.create(inputDto);

        // then
        assertNotNull(result);
        assertEquals(new LocationDto(1L,"test-slug","test-name"),result);


        verify(modelMapper, times(1)).map(inputDto, LocationEntity.class);
        verify(locationRepos, times(1)).save(mappedEntity);
        verify(modelMapper, times(1)).map(savedEntity, LocationDto.class);
        verifyNoMoreInteractions(modelMapper, locationRepos);
    }

    @Test
    void findById_LocationExists_ReturnsLocationDto() {
        // given
        Long locationId = 1L;
        LocationEntity locationEntity = new LocationEntity(locationId,"test-name","test-slug");
        LocationDto locationDto = new LocationDto(locationId, "test-name", "test-slug");
        when(locationRepos.findById(locationId)).thenReturn(Optional.of(locationEntity));
        when(modelMapper.map(locationEntity, LocationDto.class)).thenReturn(locationDto);

        // when
        var result = locationService.findById(locationId);

        // then
        assertTrue(result.isPresent());
        assertEquals(locationDto, result.get());

        verify(locationRepos, times(1)).findById(locationId);
        verify(modelMapper, times(1)).map(locationEntity, LocationDto.class);
        verifyNoMoreInteractions(locationRepos, modelMapper);
    }

    @Test
    void findById_LocationDoesNotExist_ReturnsEmptyOptional() {
        // given
        Long locationId = 1L;
        when(locationRepos.findById(locationId)).thenReturn(Optional.empty());

        // when
        var result = locationService.findById(locationId);

        // then
        assertFalse(result.isPresent());

        verify(locationRepos, times(1)).findById(locationId);
        verifyNoMoreInteractions(locationRepos);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void updateLocation_LocationExists_UpdatesSuccessfully() {
        // given
        Long locationId = 1L;
        LocationDto locationDto = new LocationDto(null, "test-slug", "test-name");
        LocationEntity locationEntity = new LocationEntity(locationId,"test-slug","test-name");
        LocationEntity updatedEntity = new LocationEntity(locationId,"new-slug","new-name");

        when(locationRepos.findById(locationId)).thenReturn(Optional.of(locationEntity));
        when(modelMapper.map(locationDto, LocationEntity.class)).thenReturn(updatedEntity);

        // when
        locationService.update(locationId, locationDto);
        // then
        verify(locationRepos, times(1)).findById(locationId);
        verify(modelMapper, times(1)).map(locationDto, LocationEntity.class);
        verify(locationRepos, times(1)).update(locationId, updatedEntity);
        verifyNoMoreInteractions(locationRepos, modelMapper);
    }

    @Test
    void updateLocation_LocationDoesNotExist_ThrowsNoSuchElementException() {
        // given
        Long locationId = 1L;
        LocationDto updatedLocationDto = new LocationDto(null, "updated-slug", "update-name");

        when(locationRepos.findById(locationId)).thenReturn(Optional.empty());

        //when
        assertThrows(NoSuchElementException.class, () ->
                locationService.update(locationId, updatedLocationDto));

        verify(locationRepos, times(1)).findById(locationId);
        verifyNoMoreInteractions(locationRepos);
        verifyNoInteractions(modelMapper);
    }


    @Test
    void deleteById_LocationExists_DeleteSuccessfully() {
        // given
        Long locationId = 1L;
        LocationEntity existingEntity = new LocationEntity(1L,"test-slug","test-name");
        existingEntity.setId(locationId);

        when(locationRepos.findById(locationId)).thenReturn(Optional.of(existingEntity));
        doNothing().when(locationRepos).deleteById(locationId);

        // when
        locationService.deleteById(locationId);

        // then
        verify(locationRepos, times(1)).findById(locationId);
        verify(locationRepos, times(1)).deleteById(locationId);
        verifyNoMoreInteractions(locationRepos);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void deleteById_LocationDoesNotExist_ThrowsNoSuchElementException() {
        // given
        Long locationId = 1L;
        when(locationRepos.findById(locationId)).thenReturn(Optional.empty());

        // when
        assertThrows(NoSuchElementException.class, () -> {
            locationService.deleteById(locationId);
        });

        // then
        verify(locationRepos, times(1)).findById(locationId);
        verifyNoMoreInteractions(locationRepos);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void findAll_NoLocationsExist_ReturnsEmptyList() {
        // given
        when(locationRepos.findAll()).thenReturn(List.of());

        // when
        var result = locationService.findAll();

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(locationRepos, times(1)).findAll();
        verifyNoMoreInteractions(locationRepos);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void findAll_LocationsExist_ReturnsListOfLocationDtos() {
        // given
        LocationEntity locationEntity1 = new LocationEntity(1L,"first-slug","first-name");
        LocationEntity locationEntity2 = new LocationEntity(2L,"second-slug","second-name");

        LocationDto locationDto1 = new LocationDto(1L,"first-slug","first-name");
        LocationDto locationDto2 = new LocationDto(2L,"second-slug","second-name");

        when(locationRepos.findAll()).thenReturn(List.of(locationEntity1, locationEntity2));
        when(modelMapper.map(locationEntity1, LocationDto.class)).thenReturn(locationDto1);
        when(modelMapper.map(locationEntity2, LocationDto.class)).thenReturn(locationDto2);

        // when
        var result = locationService.findAll();

        // then
        assertNotNull(result);
        assertEquals(List.of(locationDto1, locationDto2), result);

        verify(locationRepos, times(1)).findAll();
        verify(modelMapper, times(1)).map(locationEntity1, LocationDto.class);
        verify(modelMapper, times(1)).map(locationEntity2, LocationDto.class);
        verifyNoMoreInteractions(locationRepos, modelMapper);
    }

}
