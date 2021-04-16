package com.example.project.repository;


import com.example.project.model.Recipe;
import com.example.project.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecipeRepository extends JpaRepository<Recipe, Integer> {
    List<Recipe> findByUser(User user);
    Page<Recipe> findByCategories_categoryId(Integer category_id, Pageable pageable);
}
