package org.fintech.services;

import lombok.extern.slf4j.Slf4j;
import org.fintech.client.KudagoClient;
import org.fintech.config.Timelog;
import org.fintech.exception.NoSuchState;
import org.fintech.memento.CategoryCareTaker;
import org.fintech.memento.CategoryMemento;
import org.fintech.observer.CategoryObserver;
import org.fintech.observer.CategoryObserverManager;
import org.fintech.services.baseService.CrudServiceImpl;
import org.fintech.dto.CategoryDto;
import org.fintech.store.entity.CategoryEntity;
import org.fintech.store.repos.CategoryRepos;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Slf4j
@Service
public class CategoryServiceImpl extends CrudServiceImpl<CategoryEntity, CategoryDto, CategoryObserver> implements CategoryService {

    @Autowired
    private KudagoClient kudagoClient;
    @Autowired
    private CategoryCareTaker categoryCareTaker;

    public CategoryServiceImpl(CategoryRepos categoryRepos, ModelMapper modelMapper,CategoryObserverManager observerManager) {
        super(categoryRepos, CategoryEntity.class, CategoryDto.class, modelMapper,observerManager);
    }

    @Override
    public void saveState() {
        categoryCareTaker.save(new CategoryMemento((CategoryRepos) this.crudRepos, LocalDateTime.now()));
    }
    @Override
    public void restoreState(LocalDateTime dateFrom, LocalDateTime dateTo) {
        var history = categoryCareTaker.getHistory(dateFrom,dateTo).orElseThrow(NoSuchState::new);
        this.crudRepos.clear();
        history.getCategoryReposState().findAll().forEach(this.crudRepos::save);
    }

    @Override
    public void restoreState(int id) {
        var history = categoryCareTaker.getHistoryById(id).orElseThrow(NoSuchState::new);
        this.crudRepos.clear();
        history.getCategoryReposState().findAll().forEach(this.crudRepos::save);
    }

    @Override
    @Timelog
    @EventListener(ApplicationReadyEvent.class)
    public void init(ApplicationReadyEvent event) {
        List<CategoryEntity> categories = kudagoClient.getCategories();
        for (CategoryEntity categoryEntity : categories) {
            crudRepos.save(categoryEntity);
        }
    }
}
