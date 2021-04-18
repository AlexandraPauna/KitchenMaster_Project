package com.example.project.service;

import com.example.project.model.Rating;
import com.example.project.model.Recipe;
import com.example.project.model.User;

import java.util.List;

public interface RatingService {
    Rating saveRating(Rating rating);
    boolean existsByUserAndRecipe(User user, Recipe recipe);
    List<Rating> getAllRatingsForLoggedUser(User user);
    List<Rating> getAllRatingsForRecipe(Recipe recipe);
    Rating findRatingById(Integer id);
}
