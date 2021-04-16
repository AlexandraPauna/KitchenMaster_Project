package com.example.project.repositories;

import com.example.project.model.Category;
import com.example.project.model.User;
import com.example.project.repository.UserRepository;
import org.junit.Test;
import org.junit.jupiter.api.Order;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTestIntegration {
    @Autowired
    UserRepository userRepository;

    @Test
    @Transactional
    @Rollback(value = false)
    public void t1_saveUser() throws Exception {

        User user = User.builder()
                .id(101)
                .userName("TestU")
                .firstName("Test1")
                .lastName("Test")
                .email("test@test.com")
                .password("Parola1!")
                .build();

        User savedUser = userRepository.save(user);

        assertThat(savedUser.getId()).isGreaterThan(0);
    }

    @Test
    @Order(2)
    public void t2_findByName() {
        User optUser = userRepository.findByUserName("TestU");
        assertThat(optUser.getUserName()).isEqualTo("TestU");
    }
}
