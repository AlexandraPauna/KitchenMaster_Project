package com.example.project.services;

import com.example.project.model.Category;
import com.example.project.model.User;
import com.example.project.repository.RoleRepository;
import com.example.project.repository.UserRepository;
import com.example.project.service.UserService;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    @Mock
    private UserRepository mockUserRepository;
    @Mock
    private RoleRepository mockRoleRepository;
    @Mock
    private BCryptPasswordEncoder mockBCryptPasswordEncoder;

    private UserService userServiceUnderTest;
    private User user;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        userServiceUnderTest = new UserService(mockUserRepository,
                mockRoleRepository,
                mockBCryptPasswordEncoder);
        user = User.builder()
                .id(1)
                .userName("TestU")
                .firstName("Test1")
                .lastName("Test")
                .email("test@test.com")
                .build();

        Mockito.when(mockUserRepository.save(any()))
                .thenReturn(user);
        Mockito.when(mockUserRepository.findByEmail(anyString()))
                .thenReturn(user);
        Mockito.when(mockUserRepository.findByUserName(anyString()))
                .thenReturn(user);
    }

    @Test
    public void testFindUserByEmail() {
        // Setup
        final String email = "test@test.com";

        // Run the test
        final User result = userServiceUnderTest.findUserByEmail(email);

        // Verify the results
        assertEquals(email, result.getEmail());
    }

    @Test
    public void testFindUserByUserName() {
        // Setup
        final String value = "TestU";

        // Run the test
        final User result = userServiceUnderTest.findUserByUserName(value);

        // Verify the results
        assertEquals(value, result.getUserName());
    }

    @Test
    public void testSaveUser() {
        // Setup
        final String email = "test@test.com";

        // Run the test
        User result = userServiceUnderTest.saveUser(User.builder().build());

        // Verify the results
        assertEquals(email, result.getEmail());
    }

    @Test
    public void testUpdateUser() {
        // Setup
        final String value = "UserUpd";
        user.setLastName(value);

        // Run the test
        final User result = userServiceUnderTest.updateUser(User.builder().build());

        // Verify the results
        assertEquals(value, result.getLastName());
    }
}
