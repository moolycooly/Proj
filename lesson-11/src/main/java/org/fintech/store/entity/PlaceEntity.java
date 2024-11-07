package org.fintech.store.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(exclude={"events"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(schema = "events_places", name = "place")

public class PlaceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name = "slug", nullable = false)
    private String slug;
    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EventEntity> events;


}
