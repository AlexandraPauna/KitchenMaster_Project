package com.example.project.service;

import com.example.project.model.Recipe;
import com.example.project.model.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface RecipeService {
    List<Recipe> getAllRecipes();
    Recipe saveRecipe(Recipe recipe);
    List<Recipe> getAllRecipesForLoggedUser(User user);
    Recipe findRecipeById(Integer id);
    Page<Recipe> getAllRecipesByCategoryPage(Integer category_id, Integer pageNumber, String sortKey);
    void deleteById(int id);
    Recipe updateRecipe(Recipe recipe);
}
