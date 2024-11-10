package org.fintech.store.repository;

import org.fintech.store.entity.EventEntity;
import org.fintech.store.entity.PlaceEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public interface EventRepository extends JpaRepository<EventEntity,Integer> {
    List<EventEntity> findAll(Specification<EventEntity> specification);

    static Specification<EventEntity> buildSpecification(String name, String description,
                                                         LocalDate dateFrom, LocalDate dateTo, PlaceEntity placeEntity) {
        List<Specification<EventEntity>> specs = new ArrayList<>();

        if(placeEntity!=null) {
            specs.add((Specification<EventEntity>) (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("place"), placeEntity));

        }
        if (name != null) {
            specs.add((Specification<EventEntity>) (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("name"), name));
        }
        if (description != null) {
            specs.add((Specification<EventEntity>) (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("description"), "%" + description + "%"));
        }
        if (dateFrom != null) {
            specs.add((Specification<EventEntity>) (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("date"), dateFrom));
        }
        if (dateTo != null) {
            specs.add((Specification<EventEntity>) (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("date"), dateTo));
        }
        return specs.stream().reduce(Specification::and).orElse(null);
    }
}
