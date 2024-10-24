package org.fintech.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.fintech.controllers.payload.NewPlacePayload;
import org.fintech.controllers.payload.UpdatePlacePayload;

import org.fintech.exception.PlaceNotFoundException;
import org.fintech.dto.PlaceDto;
import org.fintech.services.PlaceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/place")
@Tag(name="Place Controller")
public class PlaceRestController {
    private final PlaceService placeService;

    @Operation(
            summary = "Get all places",
            responses ={
                    @ApiResponse(responseCode = "200", description = "Success",
                            content = @Content(mediaType = "application/json", array=@ArraySchema(schema = @Schema(implementation = PlaceDto.class))))
            }
    )
    @GetMapping
    public List<PlaceDto> getAllPlaces() {
        return placeService.findAllPlaces();
    }
    @Operation(
            summary = "Get place by id",
            responses ={
                    @ApiResponse(responseCode = "200", description = "Success",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PlaceDto.class))),
                    @ApiResponse(responseCode = "404", description = "Place was not found",
                            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @GetMapping("/{id}")
    public PlaceDto getPlaceById(@PathVariable(name = "id") int id) {
        return placeService.findPlaceById(id).orElseThrow(() -> new PlaceNotFoundException(id));
    }
    @Operation(
            summary = "Create place",
            responses ={
                    @ApiResponse(responseCode = "200", description = "Success",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PlaceDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid payload",
                            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class))),
            }
    )
    @PostMapping
    public ResponseEntity<PlaceDto> createPlace(@RequestBody @Valid NewPlacePayload newPlace) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(placeService.createPlace(newPlace.slug(), newPlace.name()));
    }
    @Operation(
            summary = "Update place by id",
            responses ={
                    @ApiResponse(responseCode = "200", description = "Success",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = void.class))),
                    @ApiResponse(responseCode = "404", description = "Place was not found",
                            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @PutMapping("/{id}")
    public void updatePlace(@RequestBody @Valid UpdatePlacePayload updatePlace, @PathVariable(name = "id") int id) {
        placeService.updatePlace(id, updatePlace.slug(), updatePlace.name());
    }
    @Operation(
            summary = "Delete place by id",
            responses ={
                    @ApiResponse(responseCode = "204", description = "Success",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = void.class))),
                    @ApiResponse(responseCode = "404", description = "Place was not found",
                            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePlace(@PathVariable(name = "id") int id) {
        placeService.deletePlaceById(id);
        return ResponseEntity.noContent().build();
    }

}
