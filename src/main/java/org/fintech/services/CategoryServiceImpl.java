package org.fintech.services;

import lombok.extern.slf4j.Slf4j;
import org.fintech.client.KudagoClient;
import org.fintech.config.Timelog;
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

import java.util.List;
@Slf4j
@Service
public class CategoryServiceImpl extends CrudServiceImpl<CategoryEntity, CategoryDto, CategoryObserver> implements CategoryService {

    @Autowired
    private KudagoClient kudagoClient;
    public CategoryServiceImpl(CategoryRepos categoryRepos, ModelMapper modelMapper,CategoryObserverManager observerManager) {
        super(categoryRepos, CategoryEntity.class, CategoryDto.class, modelMapper,observerManager);
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
