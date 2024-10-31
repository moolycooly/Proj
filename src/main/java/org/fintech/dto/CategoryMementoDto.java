package org.fintech.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CategoryMementoDto {
    private int id;
    private LocalDateTime date;
}
