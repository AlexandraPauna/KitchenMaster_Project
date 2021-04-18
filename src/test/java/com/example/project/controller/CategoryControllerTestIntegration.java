package com.example.project.controller;

import com.example.project.model.Category;
import com.example.project.service.CategoryService;
import com.example.project.service.RecipeService;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.zalando.problem.ProblemModule;
import org.zalando.problem.validation.ConstraintViolationProblemModule;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

    @MockBean
    private RecipeService recipeService;

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
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/category/index"))
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

    //SHOW Category
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
    public void updateCategory_Return200_WhenUpdateCategory_Succes() throws Exception {
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
    public void updateCategory_NameWrong() throws Exception {
        final Integer categId = 1;
        final Category category = new Category();
        category.setCategoryId(categId);

        when(categoryService.findCategoryById(categId)).thenReturn(category);

        mockMvc.perform(post("/category/update/{id}", category.getCategoryId())
                .contentType(APPLICATION_FORM_URLENCODED))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("category","name"))
                .andExpect(model().attributeHasFieldErrorCode("category", "name", "NotEmpty"))
                .andExpect(status().isOk())
                .andExpect(view().name("/category/update"))
        ;
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

    //INDEX Category
    //vor exista 4 liste deoarece categoriile vor aparea impartite pe 4 coloane
    //deoarece exista 2 categorii, primele 2 vor contine cate una, iar celelalte 2 vor fi goale
    @Test
    public void shouldFetchAllCategories_In2Lists() throws Exception {
        List<Category> categList;

        categList = new ArrayList<>();
        Category category2 = new Category();
        category2.setCategoryId(2);
        category2.setName("Categorie Test_2");
        Category category3 = new Category();
        category3.setCategoryId(3);
        category3.setName("Categorie Test_3");
        categList.add(category2);
        categList.add(category3);

        given(categoryService.getAllCategories()).willReturn(categList);
        List<Category> expectedCategList1 = new ArrayList<Category>();
        expectedCategList1.add(categList.get(0));
        List<Category> expectedCategList2 = new ArrayList<Category>();
        expectedCategList2.add(categList.get(1));

        this.mockMvc.perform(get("/category/index"))
                .andExpect(model().attribute("categoriesList1", expectedCategList1))
                .andExpect(model().attribute("categoriesList2", expectedCategList2))
                .andExpect(model().attribute("categoriesList3", is(nullValue())))
                .andExpect(model().attribute("categoriesList4", is(nullValue())))
                .andExpect(status().isOk())
                .andExpect(view().name("/category/index"))
        ;
    }

    //DELETE
    //visitor cannot delete category, will be redirected to login page
    @Test
    public void testCategoryDelete_Redirect() throws Exception {

        mockMvc.perform(delete("/category/{id}/delete", 1).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("http://localhost/login"))
        ;
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    public void testCategoryDeleteByUser_Redirect() throws Exception {

        mockMvc.perform(delete("/category/{id}/delete", 1).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
        ;
    }

    //admin can delete category
    @Test
    @WithMockUser(authorities = {"ADMIN"})
    public void testCategoryDeleteByAdmin_Succes() throws Exception {

        mockMvc.perform(delete("/category/{id}/delete", 1).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/category/index"))
        ;
    }
}
