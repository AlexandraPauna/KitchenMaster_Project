package com.example.project.controller;

import com.example.project.model.Category;
import com.example.project.model.Role;
import com.example.project.model.User;
import com.example.project.repository.RoleRepository;
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

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"mysql"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private RoleRepository roleRepository;

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

    //PROFILE
    //an user that is not logged in cannot acces profile page
    @Test
    public void shouldReturn_Redirect_NoUserLoggedIn() throws Exception {

        this.mockMvc.perform(get("/profile"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("http://localhost/login"))
        ;
    }

    //when a logged in user tries to acces the profile page, he will be permitted
    @Test
    @WithMockUser(authorities = "ADMIN")
    public void shouldReturn_200_WhenCreateCategory_LoggedAdmin() throws Exception {

        final User user = new User();
        user.setId(101);
        user.setUserName("TestU");
        user.setFirstName("Test1");
        user.setLastName("Test");
        user.setEmail("test@test.com");
        user.setPassword("Parola1!");

        Role adminRole = roleRepository.findByRole("ADMIN");
        user.setRoles(new HashSet<Role>(Arrays.asList(adminRole)));

        when(userService.findUserByUserName(any())).thenReturn(user);

        this.mockMvc.perform(get("/profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("/profile"))
        ;
    }

    //UPDATE User

    //when a user that is not logged in tries to acces the edit profile page
    @Test
    public void updateUser_Redirect_NoUserLoggedIn() throws Exception {
        final User user = new User();
        user.setId(101);
        user.setUserName("TestU");
        user.setFirstName("Test1");
        user.setLastName("Test");
        user.setEmail("test@test.com");
        user.setPassword("Parola1!");
        Role adminRole = roleRepository.findByRole("ADMIN");
        user.setRoles(new HashSet<Role>(Arrays.asList(adminRole)));

        when(userService.findUserByUserName(any())).thenReturn(user);

        this.mockMvc.perform(get("/user/update/{id}", 101))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("http://localhost/login"))
        ;
    }

     //when a logged in user tries to acces the profile page, he will be permitted
    @Test
    @WithMockUser
    public void updateCategory_Return200_Logged() throws Exception {
        final User user = new User();
        user.setId(101);
        user.setUserName("TestU");
        user.setFirstName("Test1");
        user.setLastName("Test");
        user.setEmail("test@test.com");
        user.setPassword("Parola1!");
        Role adminRole = roleRepository.findByRole("ADMIN");
        user.setRoles(new HashSet<Role>(Arrays.asList(adminRole)));

        when(userService.findUserByUserName(any())).thenReturn(user);
        this.mockMvc.perform(get("/user/update/{id}", 101))
                .andExpect(status().isOk())
                .andExpect(view().name("/user/update"))
        ;
    }
}
