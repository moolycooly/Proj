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
public class LocationDto extends AbstractDto{
    Long id;
    @NotNull
    @Size(min = 1, max = 50)
    String slug;
    @Size(min = 1, max = 50)
    @NotNull
    String name;
}
