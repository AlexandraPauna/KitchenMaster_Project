package com.example.project.services;

import com.example.project.model.Category;
import com.example.project.model.User;
import com.example.project.repository.CategoryRepository;
import com.example.project.service.CategoryService;
import com.example.project.service.impl.CategoryServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class CategorySerivceImplTest {

    CategoryService categoryService;
    private Category category;

    @Mock
    CategoryRepository categoryRepository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        categoryService = new CategoryServiceImpl(categoryRepository);
        category = Category.builder()
                .categoryId(1)
                .name("Categorie Test")
                .build();
        Mockito.when(categoryRepository.saveAndFlush(any()))
                .thenReturn(category);
        Mockito.when(categoryRepository.findById(anyInt()))
                .thenReturn(Optional.of(category));
        Mockito.when(categoryRepository.save(any()))
                .thenReturn(category);
    }

    @Test
    public void testSavedCategory() {
        //Setup
        final String name = "Categorie Test";

        // Run the test
        Category result = categoryService.saveCategory(Category.builder().build());

        // Verify the results
        assertEquals(name, result.getName());
    }

    @Test
    public void testFindCategoryById() {
        // Setup
        final Integer value = 1;

        // Run the test
        final Category result = categoryService.findCategoryById(1);

        // Verify the results
        assertEquals(value, result.getCategoryId());
    }

    @Test
    public void testUpdateCategory() {
        // Setup
        final String value = "Categorie Sch";
        category.setName(value);

        // Run the test
        final Category result = categoryService.updateCategory(Category.builder().build());

        // Verify the results
        assertEquals(value, result.getName());
    }

    @Test
    public void testGetAllCategories() {
        // Setup
        Category category2 = Category.builder()
                .categoryId(2)
                .name("Categorie Test_2")
                .build();
        Category category3 = Category.builder()
                .categoryId(3)
                .name("Categorie Test_3")
                .build();
        List<Category> categList = new ArrayList<Category>();
        categList.add(category);
        categList.add(category2);
        categList.add(category3);
        Mockito.when(categoryRepository.findAll())
                .thenReturn(categList);

        // Run the test
        final List<Category> result = categoryService.getAllCategories();

        // Verify the results
        assertEquals(categList.size(), result.size());
    }
}
