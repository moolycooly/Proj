package org.fintech.controllers.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record NewEventPayload(@NotNull Integer placeId, @NotBlank String name, String description, @NotNull LocalDate date){
}
