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
}
