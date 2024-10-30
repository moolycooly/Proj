package org.fintech.services.baseService;

import lombok.RequiredArgsConstructor;

import org.fintech.dto.AbstractDto;
import org.fintech.observer.Observer;
import org.fintech.observer.ObserverManager;
import org.fintech.store.entity.AbstractEntity;
import org.fintech.store.repos.baseRepos.CrudRepos;
import org.modelmapper.ModelMapper;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
public abstract class CrudServiceImpl<E extends AbstractEntity, D extends AbstractDto, O extends Observer<D>> implements CrudService<D,Long> {
    protected final CrudRepos<E,Long> crudRepos;
    protected final Class<E> entityClass;
    protected final Class<D> dtoClass;
    protected final ModelMapper modelMapper;
    protected final ObserverManager<D,O> observerManager;

    @Override
    public D create(D dto)  {
        E entity = crudRepos.save(modelMapper.map(dto, entityClass));
        var created = modelMapper.map( entity, dtoClass);
        observerManager.notifyObservers(LocalDateTime.now(),created);
        return created;
    }

    @Override
    public Optional<D> findById(Long id) {
        return crudRepos.findById(id)
                .map(entity -> modelMapper.map(entity, dtoClass));
    }

    @Override
    public void update(Long id, D dto) {
        isExistEntity(id);
        crudRepos.update(id, modelMapper.map(dto, entityClass));

    }

    @Override
    public void deleteById(Long id) {
        isExistEntity(id);
        crudRepos.deleteById(id);
    }

    @Override
    public List<D> findAll() {
        List<D> dtoList = new ArrayList<>();
        for(E entity : crudRepos.findAll()) {
            dtoList.add(modelMapper.map(entity, dtoClass));
        }
        return dtoList;
    }
    public void isExistEntity(Long id) {
        crudRepos.findById(id)
                .orElseThrow(NoSuchElementException::new);
    }
}
