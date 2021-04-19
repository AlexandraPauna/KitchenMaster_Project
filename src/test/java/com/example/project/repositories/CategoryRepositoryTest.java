package com.example.project.repositories;

import com.example.project.model.Category;
import com.example.project.repository.CategoryRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(OrderAnnotation.class)
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    public Integer generatedId;

    @Test
    //@Transactional
    @Rollback(false)
    @Order(1)
    public void t1_saveCategory() throws Exception {

        Category category = new Category();
        category.setName("CategorieTest");
        //category.setCategoryId(101);
        Category savedCategory = categoryRepository.saveAndFlush(category);
        generatedId = savedCategory.getCategoryId();

        assertThat(savedCategory.getCategoryId()).isGreaterThan(0);
    }

    @Test
    @Rollback(value = false)
    @Order(2)
    public void t2_findByName() {
        Optional<Category> optCategory = categoryRepository.findByName("CategorieTst");
        assertThat(Optional.ofNullable(optCategory.get().getName())).isEqualTo(Optional.ofNullable("CategorieTst"));
    }

    @Test
    @Rollback(value = false)
    @Order(3)
    public void t3_findById() {
        Optional<Category> optCategory = categoryRepository.findById(48);
        assertThat(Optional.ofNullable(optCategory.get().getCategoryId())).isEqualTo(Optional.ofNullable(48));
    }

    @Test
    @Rollback(false)
    @Order(4)
    public void t4_updateCategory() {
        Optional<Category> optCategory = categoryRepository.findByName("CategorieTest");
        if(optCategory.isPresent()){
            Category cat = optCategory.get();
            cat.setName("CategSchimbata");
            categoryRepository.save(cat);

            Optional<Category> updatedCategory = categoryRepository.findByName("CategSchimbata");
            assertThat(Optional.ofNullable(updatedCategory.get().getName())).isEqualTo(Optional.ofNullable("CategSchimbata"));
        }
    }

    @Test
    @Rollback(false)
    @Order(5)
    public void t5_getAllCategories() {
        List<Category> categories = (List<Category>) categoryRepository.findAll();
        assertThat(categories.size()).isGreaterThan(0);
    }

    @Test
    @Rollback(false)
    @Order(6)
    public void t6_deleteCategory() {
        Optional<Category> optCategory = categoryRepository.findByName("CategorieSchimbata");
        if(optCategory.isPresent()){
            Category cat = optCategory.get();
            categoryRepository.deleteById(cat.getCategoryId());

            Optional<Category> deletedCategory = categoryRepository.findByName("CategorieSchimbata");
            assertThat(deletedCategory).isNull();
        }

    }

}
