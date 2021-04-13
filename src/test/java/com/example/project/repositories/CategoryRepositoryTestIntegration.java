package com.example.project.repositories;

import com.example.project.model.Category;
import com.example.project.repository.CategoryRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CategoryRepositoryTestIntegration {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    CategoryRepository categoryRepository;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    @Transactional
    @Rollback(value = false)
    @Order(1)
    public void saveCategory() throws Exception {

        Category category = new Category();
        category.setName("CategorieTest");
        Category savedCategory = categoryRepository.saveAndFlush(category);

        assertThat(savedCategory.getCategoryId()).isGreaterThan(0);
    }

//    @Test
//    public void findByName() {
//        Optional<Category> optCategory = categoryRepository.findByName("Vegan");
//        assertEquals(Optional.ofNullable(optCategory.get().getName()), "Vegan");
//    }
}
