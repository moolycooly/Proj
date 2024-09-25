package org.fintech.store.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LocationEntity extends AbstractEntity{
    Long id;
    String slug;
    String name;
}
