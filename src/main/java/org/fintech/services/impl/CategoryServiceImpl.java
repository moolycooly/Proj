package org.fintech.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.fintech.client.KudagoClient;
import org.fintech.services.CategoryService;
import org.fintech.services.baseService.CrudServiceImpl;
import org.fintech.dto.CategoryDto;
import org.fintech.store.entity.CategoryEntity;
import org.fintech.store.repos.CategoryRepos;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CategoryServiceImpl extends CrudServiceImpl<CategoryEntity, CategoryDto> implements CategoryService {
    @Autowired
    private KudagoClient kudagoClient;

    public CategoryServiceImpl(CategoryRepos categoryRepos, ModelMapper modelMapper) {
        super(categoryRepos, CategoryEntity.class, CategoryDto.class, modelMapper);
    }
    @Override
    public void init() {
        List<CategoryEntity> categories = kudagoClient.getCategories();
        for (CategoryEntity categoryEntity : categories) {
            crudRepos.save(categoryEntity);
        }
    }
}
