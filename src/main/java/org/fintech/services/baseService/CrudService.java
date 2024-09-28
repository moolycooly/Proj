package org.fintech.services.baseService;

import java.util.List;
import java.util.Optional;

public interface CrudService<D,V> {
    D create(D entity);
    Optional<D> findById(V id);
    void update(V id, D entity);
    void deleteById(V id);
    List<D> findAll();
}
