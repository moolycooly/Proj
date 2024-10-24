package org.fintech.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.fintech.controllers.payload.NewPlacePayload;
import org.fintech.controllers.payload.UpdatePlacePayload;
import org.fintech.exception.PlaceNotFoundException;
import org.fintech.dto.PlaceDto;
import org.fintech.services.PlaceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/place")
public class PlaceRestController {
    private final PlaceService placeService;
    @GetMapping
    public List<PlaceDto> getAllPlaces() {
        return placeService.findAllPlaces();
    }
    @GetMapping("/{id}")
    public PlaceDto getPlaceById(@PathVariable(name = "id") int id) {
        return placeService.findPlaceById(id).orElseThrow(() -> new PlaceNotFoundException(id));
    }
    @PostMapping
    public ResponseEntity<PlaceDto> createSeller(@RequestBody @Valid NewPlacePayload newPlace) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(placeService.createPlace(newPlace.slug(), newPlace.name()));
    }

    @PutMapping("/{id}")
    public void updatePlace(@RequestBody @Valid UpdatePlacePayload updatePlace, @PathVariable(name = "id") int id) {
        placeService.updatePlace(id, updatePlace.slug(), updatePlace.name());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSeller(@PathVariable(name = "id") int id) {
        placeService.deletePlaceById(id);
        return ResponseEntity.noContent().build();
    }

}
