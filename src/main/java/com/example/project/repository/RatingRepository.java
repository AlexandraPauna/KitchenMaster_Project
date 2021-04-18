package com.example.project.repository;

import com.example.project.model.Rating;
import com.example.project.model.Recipe;
import com.example.project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Integer> {
    boolean existsByUserAndRecipe(User user, Recipe recipe);
    List<Rating> findByUser(User user);
    List<Rating> findByRecipe(Recipe recipe);
}

