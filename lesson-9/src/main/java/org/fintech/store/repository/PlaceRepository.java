package org.fintech.store.repository;

import org.fintech.store.entity.PlaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepository extends JpaRepository<PlaceEntity, Integer> {
}
