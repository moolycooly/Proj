package org.fintech.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationDto extends AbstractDto{
    private Long id;
    @NotNull
    @Size(min = 1, max = 50)
    private String slug;
    @Size(min = 1, max = 50)
    @NotNull
    private String name;
}
