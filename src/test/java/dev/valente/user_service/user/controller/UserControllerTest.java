package dev.valente.user_service.user.controller;

import dev.valente.user_service.user.common.UserDataUtil;
import dev.valente.user_service.user.repository.UserData;
import dev.valente.user_service.user.repository.UserRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(UserController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserControllerTest {

    @MockBean
    private UserData userData;

    @SpyBean
    private UserRepository userRepository;

    @Autowired
    private UserDataUtil userDataUtil;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void findAll() {
    }

    @Test
    void findById() {
    }

    @Test
    void findWithParams() {
    }

    @Test
    void save() {
    }

    @Test
    void replace() {
    }

    @Test
    void deleteById() {
    }
}