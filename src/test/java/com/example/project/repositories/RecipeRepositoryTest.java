package com.example.project.repositories;

import com.example.project.model.Category;
import com.example.project.model.Recipe;
import com.example.project.repository.CategoryRepository;
import com.example.project.repository.RecipeRepository;
import org.junit.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RecipeRepositoryTest {

    @Autowired
    RecipeRepository recipeRepository;

    @Test
    @Transactional
    @Rollback(value = false)
    @Order(1)
    public void t1_saveRecipe() throws Exception {

        Recipe recipe = new Recipe();
        recipe.setName("RetetaTest");
        recipe.setId(101);
        recipe.setServing(4);
        recipe.setCalories(100);
        recipe.setDescription("Test1");
        recipe.setPreparation_time(60);

        Category category = new Category();
        category.setName("CategorieTest");
        category.setCategoryId(1);

        List<Category> listCateg = Arrays.asList(category);
        Set<Category> categories = new HashSet<>(listCateg);

        recipe.setCategories(categories);
        Recipe savedRecipe = recipeRepository.saveAndFlush(recipe);

        assertThat(savedRecipe.getId()).isGreaterThan(0);
    }

    @Test
    @Order(2)
    public void t2_findById() {
        Optional<Recipe> optRecipe = recipeRepository.findById(101);
        assertThat(Optional.ofNullable(optRecipe.get().getId())).isEqualTo(Optional.ofNullable(101));
    }

    @Test
    @Rollback(false)
    @Order(3)
    public void t3_updateRecipe() {
        Optional<Recipe> optRecipe = recipeRepository.findById(101);
        if(optRecipe.isPresent()){
            Recipe rec = optRecipe.get();
            rec.setName("RetetaSchimbata");
            recipeRepository.save(rec);

            Optional<Recipe> updatedRecipe = recipeRepository.findById(101);
            assertThat(Optional.ofNullable(updatedRecipe.get().getName())).isEqualTo(Optional.ofNullable("RetetaSchimbata"));
        }
    }

    @Test
    @Rollback(false)
    @Order(4)
    public void t4_deleteRecipe() {
        Optional<Recipe> optRecipe = recipeRepository.findById(101);
        if(optRecipe.isPresent()){
            Recipe recipe = optRecipe.get();
            recipeRepository.deleteById(recipe.getId());

            Optional<Recipe> deletedRecipe = recipeRepository.findById(101);
            assertThat(deletedRecipe).isNull();
        }

    }
}
