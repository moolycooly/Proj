package org.fintech.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.fintech.config.Timelog;
import org.fintech.dto.LocationDto;
import org.fintech.services.LocationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@Timelog
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/locations")
public class LocationRestController {
    private final LocationService locationService;
    @GetMapping
    public List<LocationDto> getLocations() {
        return locationService.findAll();
    }
    @GetMapping("/{id}")
    public LocationDto getLocation(@PathVariable("id") Long id) {
        return locationService
                .findById(id)
                .orElseThrow(()->new NoSuchElementException("Location with id: " + id + " not found"));
    }
    @PostMapping
    public ResponseEntity<LocationDto> createLocation(@Valid @RequestBody LocationDto requestLocation) {
        LocationDto location = locationService.create(requestLocation);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(location);
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateLocation(@PathVariable("id") Long id, @Valid @RequestBody LocationDto requestLocation) {
        requestLocation.setId(id);
        locationService.update(id,requestLocation);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLocation(@PathVariable("id") Long id) {
        locationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
