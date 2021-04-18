package com.example.project.services;

import com.example.project.model.Recipe;
import com.example.project.repository.RecipeRepository;
import com.example.project.service.RecipeService;
import com.example.project.service.impl.RecipeServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

public class RecipeServiceImplTest {
    RecipeService recipeService;
    private Recipe recipe;

    @Mock
    RecipeRepository recipeRepository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        recipeService = new RecipeServiceImpl(recipeRepository);
        recipe = Recipe.builder()
                .id(1)
                .name("Recipe Test")
                .build();
        Mockito.when(recipeRepository.saveAndFlush(any()))
                .thenReturn(recipe);
        Mockito.when(recipeRepository.findById(anyInt()))
                .thenReturn(Optional.of(recipe));
        Mockito.when(recipeRepository.save(any()))
                .thenReturn(recipe);
    }

    @Test
    public void testSavedRecipe() {
        //Setup
        final String name = "Recipe Test";

        // Run the test
        Recipe result = recipeService.saveRecipe(Recipe.builder().build());

        // Verify the results
        assertEquals(name, result.getName());
    }

    @Test
    public void testFindRecipeById() {
        // Setup
        final Integer value = 1;

        // Run the test
        final Recipe result = recipeService.findRecipeById(1);

        // Verify the results
        assertEquals(value, result.getId());
    }

    @Test
    public void testUpdateRecipe() {
        // Setup
        final String value = "Recipe Changed";
        recipe.setName(value);

        // Run the test
        final Recipe result = recipeService.updateRecipe(Recipe.builder().build());

        // Verify the results
        assertEquals(value, result.getName());
    }

}
