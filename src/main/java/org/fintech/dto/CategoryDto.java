package org.fintech.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryDto extends AbstractDto {
    Long id;
    @Size(min = 1, max = 50)
    @NotNull
    String slug;
    @Size(min = 1, max = 50)
    @NotNull
    String name;
}
