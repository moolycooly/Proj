package org.fintech.controllers.payload;

import java.time.LocalDate;

public record UpdateEventPayload(String name, String description, LocalDate date) {
}
