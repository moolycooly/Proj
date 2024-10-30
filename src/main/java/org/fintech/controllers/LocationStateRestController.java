package org.fintech.controllers;

import lombok.RequiredArgsConstructor;
import org.fintech.dto.LocationMementoDto;
import org.fintech.memento.LocationCareTaker;
import org.fintech.services.LocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/state/location")
public class LocationStateRestController {
    private final LocationService locationService;
    private final LocationCareTaker locationCareTaker;
    @GetMapping
    public List<LocationMementoDto> getStatesTime() {
        return locationCareTaker.getHistory().entrySet().stream().map(v->new LocationMementoDto(v.getKey(),v.getValue().getDate())).toList();
    }
    @PostMapping()
    public ResponseEntity<?> saveLocationState() {
        locationService.saveState();
        return ResponseEntity.ok("State was saved");
    }
    @GetMapping("/restore")
    public ResponseEntity<?> restoreLocationState(@PathVariable("dateFrom") LocalDateTime dateFrom, @PathVariable("dateTo") LocalDateTime dateTo) {
        locationService.restoreState(dateFrom,dateTo);
        return ResponseEntity.ok("State was restored");
    }
    @GetMapping("/restore/{id}")
    public ResponseEntity<?> restoreLocationState(@PathVariable("id") int id) {
        locationService.restoreState(id);
        return ResponseEntity.ok("State was restored");
    }
}
