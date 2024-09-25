package org.fintech.services.baseService;

import lombok.RequiredArgsConstructor;

import org.fintech.dto.AbstractDto;
import org.fintech.store.entity.AbstractEntity;
import org.fintech.store.repos.baseRepos.CrudRepos;
import org.modelmapper.ModelMapper;


import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
public abstract class CrudServiceImpl<E extends AbstractEntity, D extends AbstractDto> implements CrudService<D,Long> {
    protected final CrudRepos<E,Long> crudRepos;
    protected final Class<E> entityClass;
    protected final Class<D> dtoClass;
    protected final ModelMapper modelMapper;

    @Override
    public D create(D dto)  {
        E entity = crudRepos.save(modelMapper.map(dto, entityClass));
        return modelMapper.map( entity, dtoClass);
    }

    @Override
    public Optional<D> findById(Long id) {
        return crudRepos.findById(id)
                .map(entity -> modelMapper.map(entity, dtoClass));
    }

    @Override
    public void update(Long id, D dto) {
        this.isExistEntity(id);
        crudRepos.update(id, modelMapper.map(dto, entityClass));

    }

    @Override
    public void deleteById(Long id) {
        this.isExistEntity(id);
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
                .orElseThrow(() -> new NoSuchElementException(String
                        .format("%s with id: %d doesnt exists",
                                entityClass.getSimpleName(), id)));
    }
}
