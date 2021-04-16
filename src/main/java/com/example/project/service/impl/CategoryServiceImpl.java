package com.example.project.service.impl;

import com.example.project.model.Category;
import com.example.project.repository.CategoryRepository;
import com.example.project.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Category saveCategory(Category category) {
        return categoryRepository.saveAndFlush(category);
    }

    @Override
    public Category findCategoryById(Integer id) {
        Optional<Category> categoryOptional = categoryRepository.findById(id);

        if(!categoryOptional.isPresent()){
            throw new  RuntimeException("Category not found!");
        }
        return categoryOptional.get();
    }

    @Override
    public Category updateCategory(Category category) {
        //Category savedCategory = categoryRepository.save(category);
        //return savedCategory;
        return categoryRepository.save(category);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public void deleteById(int id) {
        categoryRepository.deleteById(id);
    }
}
