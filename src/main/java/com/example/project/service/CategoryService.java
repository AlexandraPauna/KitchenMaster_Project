package com.example.project.service;

import com.example.project.model.Category;
import com.example.project.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;

public interface CategoryService{
    Category saveCategory(Category category);
    Category findCategoryById(Integer id);
    Category updateCategory(Category category);
}
