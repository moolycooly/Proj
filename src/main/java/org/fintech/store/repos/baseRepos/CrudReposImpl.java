package org.fintech.store.repos.baseRepos;

import org.fintech.store.entity.AbstractEntity;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public abstract class CrudReposImpl<T extends AbstractEntity> implements  CrudRepos<T,Long>{
    protected final Map<Long,T> dataStore = new ConcurrentHashMap<>();
    protected final AtomicLong counter = new AtomicLong();

    @Override
    public T save(T entity)  {
        if(entity.getId() == null || dataStore.containsKey(entity.getId()) ) {
            Long id = counter.incrementAndGet();
            while(dataStore.containsKey(id)) {
                id = counter.incrementAndGet();
            }
            entity.setId(id);
            dataStore.put(entity.getId(), entity);
        }
        else{
            dataStore.put(entity.getId(), entity);
        }
        return entity;
    }
    @Override
    public Optional<T> findById(Long id) {
        return Optional.ofNullable(dataStore.get(id));
    }
    @Override
    public List<T> findAll() {
        return dataStore.values()
                .stream()
                .sorted(Comparator.comparingLong(T::getId))
                .toList();
    }
    @Override
    public void deleteById(Long id) {
        dataStore.remove(id);
    }
    @Override
    public void update(Long id, T entity) {
        dataStore.put(id,entity);
    }
    @Override
    public void clear() {
        dataStore.clear();
    }
}
