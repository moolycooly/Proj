package org.fintech.store.repos.baseRepos;

import java.util.List;
import java.util.Optional;

public interface CrudRepos<T,V> {
    T save(T entity);
    Optional<T> findById(V id);
    List<T> findAll();
    void deleteById(V id);
    void update(V id, T entity);
    void clear();
}
