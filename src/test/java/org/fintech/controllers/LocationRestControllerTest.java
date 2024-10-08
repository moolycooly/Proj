package org.fintech.controllers;

import org.fintech.dto.LocationDto;
import org.fintech.services.LocationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class LocationRestControllerTest {
    @Mock
    private LocationService locationService;

    @InjectMocks
    private LocationRestController locationRestController;

    @Test
    void getLocations_LocationsDoNotExist_ReturnsEmptyList() {
        //given
        when(locationService.findAll()).thenReturn(List.of());

        //when
        var result = locationRestController.getLocations();

        //then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(locationService, times(1)).findAll();
        verifyNoMoreInteractions(locationService);
    }

    @Test
    void getLocations_LocationsExist_ReturnsListOfCategories() {
        //given
        var locationDto = new LocationDto(1L, "test-slug", "test-name");
        when(locationService.findAll()).thenReturn(List.of(locationDto));

        //when
        var result = locationRestController.getLocations();

        //then
        assertNotNull(result);
        assertEquals(List.of(locationDto), result);

        verify(locationService, times(1)).findAll();
        verifyNoMoreInteractions(locationService);
    }

    @Test
    void getLocation_LocationDoesNotExist_ReturnsNotFound() {
        //given
        Long locationId = 1L;
        when(locationService.findById(locationId)).thenReturn(Optional.empty());

        //when
        var exception = assertThrows(NoSuchElementException.class,
                () -> locationRestController.getLocation(locationId));
        //then
        assertEquals(exception.getMessage(),"Location with id: 1 not found");

        verify(locationService, times(1)).findById(locationId);
        verifyNoMoreInteractions(locationService);
    }

    @Test
    void getLocation_LocationExists_ReturnsLocation() {
        //given
        Long locationId = 1L;
        var locationDto = new LocationDto(locationId, "test-slug", "Test Category");
        when(locationService.findById(locationId)).thenReturn(Optional.of(locationDto));

        //when
        var result = locationRestController.getLocation(locationId);

        //then
        assertNotNull(result);
        assertEquals(result, locationDto);

        verify(locationService, times(1)).findById(locationId);
        verifyNoMoreInteractions(locationService);
    }

    @Test
    void createLocation_RequestIsValid_ReturnsNewLocation() {
        //given
        var locationDto = new LocationDto(1L, "test-slug", "test-name");
        when(locationService.create(locationDto)).thenReturn(locationDto);

        //when
        var response = locationRestController.createLocation(locationDto);

        //then
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(locationDto, response.getBody());

        verify(locationService, times(1)).create(locationDto);
        verifyNoMoreInteractions(locationService);
    }

    @Test
    void updateLocation_RequestIsValid_ReturnsNoContent() {
        //given
        Long locationId = 1L;
        var locationDto = new LocationDto(null, "test-slug", "test-name");
        doNothing().when(locationService).update(locationId,locationDto);

        //when
        var response = locationRestController.updateLocation(locationId, locationDto);

        //then
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        verify(locationService, times(1)).update(locationId,locationDto);
        verifyNoMoreInteractions(locationService);
    }

    @Test
    void updateLocation_LocationDoesNotExist_ThrowsNoSuchElementException() {
        //given
        Long locationId = 1L;
        var locationDto = new LocationDto(null, "test-slug", "test-name");

        doThrow(new NoSuchElementException("Location with id: " + locationId + " not found"))
                .when(locationService).update(locationId, locationDto);

        //when
        var exception = assertThrows(NoSuchElementException.class, () ->
                locationRestController.updateLocation(locationId, locationDto));

        //then
        assertEquals("Location with id: 1 not found", exception.getMessage());
        verify(locationService, times(1)).update(locationId, locationDto);
        verifyNoMoreInteractions(locationService);
    }
    @Test
    void deleteLocation_LocationExists_ReturnsNoContent() {
        //given
        Long locationId = 1L;
        doNothing().when(locationService).deleteById(locationId);

        //when
        var response = locationRestController.deleteLocation(locationId);

        //then
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        verify(locationService, times(1)).deleteById(locationId);
        verifyNoMoreInteractions(locationService);
    }
    @Test
    void deleteLocation_LocationDoesNotExist_ThrowsNoSuchElementException() {
        //given
        Long locationId = 1L;
        doThrow(new NoSuchElementException("Location with id: " + locationId + " not found"))
                .when(locationService).deleteById(locationId);

        //when
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                locationRestController.deleteLocation(locationId)
        );
        //then
        assertEquals("Location with id: 1 not found", exception.getMessage());

        verify(locationService, times(1)).deleteById(locationId);
        verifyNoMoreInteractions(locationService);
    }
}
