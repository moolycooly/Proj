package org.fintech.controllers.payload;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class EventFilter {
    private String name;
    private String description;
    private LocalDate fromDate;
    private LocalDate toDate;
    private Integer placeId;
}
