package org.fintech.controllers.payload;

import jakarta.validation.constraints.NotBlank;

public record NewPlacePayload(@NotBlank String slug, @NotBlank String name) {
}
