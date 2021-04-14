package com.example.project.controller;

import com.example.project.model.Category;
import com.example.project.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.zalando.problem.ProblemModule;
import org.zalando.problem.validation.ConstraintViolationProblemModule;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"mysql"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CategoryControllerTestIntegration {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {

        objectMapper.registerModule(new ProblemModule());
        objectMapper.registerModule(new ConstraintViolationProblemModule());
    }

    //when a user that is not logged in tries to create a new category, he will be redirected to the login page
    @Test
    public void shouldReturn_Redirect_NoUserLoggedIn() throws Exception {
        given(categoryService.saveCategory(any(Category.class))).willAnswer((invocation) -> invocation.getArgument(0));

        this.mockMvc.perform(get("/category/new"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("http://localhost/login"))
        ;
    }

    //when a user that that has th "USER" authority tries to create a new category, he will be forbidden
    @Test
    @WithMockUser(authorities = {"USER"})
    public void shouldReturn_Forbidden_WrongAuthUserLoggedIn() throws Exception {
        given(categoryService.saveCategory(any(Category.class))).willAnswer((invocation) -> invocation.getArgument(0));

        this.mockMvc.perform(get("/category/new"))
                .andExpect(status().isForbidden())
        ;
    }

    //when a user that that has th "ADMIN" authority tries to create a new category, he will be permitted
    @Test
    @WithMockUser(authorities = {"ADMIN"})
    public void shouldReturn_200_WhenCreateCategory_LoggedAdmin() throws Exception {
        given(categoryService.saveCategory(any(Category.class))).willAnswer((invocation) -> invocation.getArgument(0));

        this.mockMvc.perform(get("/category/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("/category/new"))
        ;
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    public void shouldReturn_200_WhenCreateCategory_Succes() throws Exception {
        given(categoryService.saveCategory(any(Category.class))).willAnswer((invocation) -> invocation.getArgument(0));

        this.mockMvc.perform(post("/category/new")
                .param("name", "CategorieT"))
                .andExpect(status().isOk())
                .andExpect(view().name("/category/index"))
        ;
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    public void shouldReturn_400_WhenCreateCategory_Empty() throws Exception {
        Category category = new Category();

        mockMvc.perform(post("/category/new").contentType(APPLICATION_FORM_URLENCODED))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("category","name"))
                .andExpect(model().attributeHasFieldErrorCode("category", "name", "NotEmpty"))
                .andExpect(status().isOk())
                .andExpect(view().name("/category/new"));
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    public void shouldReturn_400_WhenCreateCategory_Length() throws Exception {
        Category category = new Category();

        mockMvc.perform(post("/category/new").contentType(APPLICATION_FORM_URLENCODED)
                .param("name", "Te"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("category","name"))
                .andExpect(model().attributeHasFieldErrorCode("category", "name", "Length"))
                .andExpect(status().isOk())
                .andExpect(view().name("/category/new"));
    }

    @Test
    public void shouldFetch_CategoryById() throws Exception {
        final Integer categId = 1;
        final Category category = new Category();
        category.setCategoryId(categId);
        category.setName("CategTest");

        when(categoryService.findCategoryById(categId)).thenReturn(category);

        this.mockMvc.perform(get("/category/show/{id}", categId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("category", category))
        ;
    }

    //UPDATE Category

    //when a user that is not logged in tries to edit a category, he will be redirected to the login page
    @Test
    public void updateCategory_Redirect_NoUserLoggedIn() throws Exception {
        final Integer categId = 1;
        final Category category = new Category();
        category.setCategoryId(categId);
        category.setName("CategTest");

        when(categoryService.findCategoryById(categId)).thenReturn(category);

        this.mockMvc.perform(get("/category/update/{id}", categId))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("http://localhost/login"))
        ;
    }

    //when a user that that has th "USER" authority tries to edit a category, he will be forbidden
    @Test
    @WithMockUser(authorities = {"USER"})
    public void updateCategory_Forbidden_WrongAuthUserLoggedIn() throws Exception {
        final Integer categId = 1;
        final Category category = new Category();
        category.setCategoryId(categId);
        category.setName("CategTest");

        when(categoryService.findCategoryById(categId)).thenReturn(category);

        this.mockMvc.perform(get("/category/update/{id}", categId))
                .andExpect(status().isForbidden())
        ;
    }

    //when a user that that has th "ADMIN" authority tries to edit a category, he will be permitted
    @Test
    @WithMockUser(authorities = {"ADMIN"})
    public void updateCategory_Return200_LoggedAdmin() throws Exception {
        final Integer categId = 1;
        final Category category = new Category();
        category.setCategoryId(categId);
        category.setName("CategTest");

        when(categoryService.findCategoryById(categId)).thenReturn(category);
        this.mockMvc.perform(get("/category/update/{id}", categId))
                .andExpect(status().isOk())
                .andExpect(view().name("/category/update"))
        ;
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    public void updateCategory_Return200_WhenCreateCategory_Succes() throws Exception {
        final Integer categId = 1;
        final Category category = new Category();
        category.setCategoryId(categId);
        category.setName("CategTest");

        when(categoryService.findCategoryById(categId)).thenReturn(category);

        this.mockMvc.perform(post("/category/update/{id}", category.getCategoryId())
                .param("name", "CategorieT"))
                .andExpect(status().isOk())
                .andExpect(view().name("/category/index"))
        ;

    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    public void updateCategory_NameEmpty() throws Exception {
        Category category = new Category();

        mockMvc.perform(post("/category/new").contentType(APPLICATION_FORM_URLENCODED))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("category","name"))
                .andExpect(model().attributeHasFieldErrorCode("category", "name", "NotEmpty"))
                .andExpect(status().isOk())
                .andExpect(view().name("/category/new"));
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    public void updateCategory_NameWrongLength() throws Exception {
        final Integer categId = 1;
        final Category category = new Category();
        category.setCategoryId(categId);
        category.setName("CategTest");

        when(categoryService.findCategoryById(categId)).thenReturn(category);

        mockMvc.perform(post("/category/update/{id}", category.getCategoryId())
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("name", "Te"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("category","name"))
                .andExpect(model().attributeHasFieldErrorCode("category", "name", "Length"))
                .andExpect(status().isOk())
                .andExpect(view().name("/category/update"))
         ;
    }

}
