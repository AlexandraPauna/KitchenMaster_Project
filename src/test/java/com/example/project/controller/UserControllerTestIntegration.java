package com.example.project.controller;

import com.example.project.model.Category;
import com.example.project.model.User;
import com.example.project.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.zalando.problem.ProblemModule;
import org.zalando.problem.validation.ConstraintViolationProblemModule;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"mysql"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserControllerTestIntegration {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {

        objectMapper.registerModule(new ProblemModule());
        objectMapper.registerModule(new ConstraintViolationProblemModule());
    }

    //the form registration page can be accesed
    @Test
    public void shouldReturn_200_WhenRegister() throws Exception {

        this.mockMvc.perform(get("/registration"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration"))
        ;
    }

    @Test
    public void shouldReturn_200_WhenCreateUser() throws Exception {

        this.mockMvc.perform(post("/registration")
                .param("name", "TestU")
                .param("firstName", "Test1")
                .param("lastName", "Test")
                .param("email", "Test@test.com")
                .param("password", "Parola1!")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("registration"))
        ;
    }

    @Test
    public void shouldNotSave_WhenCreateUser_PasswordWeak() throws Exception {

        this.mockMvc.perform(post("/registration")
                .param("name", "TestU")
                .param("firstName", "Test1")
                .param("lastName", "Test")
                .param("email", "Test@test.com")
                .param("password", "1234")
        )
                .andExpect(model().attributeHasFieldErrors("user","password"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration"))
        ;
    }
}
