package dev.valente.user_service.user.repository;

import dev.valente.user_service.user.common.UserDataUtil;
import dev.valente.user_service.user.domain.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Import(UserDataUtil.class)
@Sql("/sql/init_sql_user")
class UserRepositoryJPATest {

    @Autowired
    private UserRepositoryJPA userRepository;

    @Autowired
    private UserDataUtil userDataUtil;

//    @Autowired
//    private TestEntityManager entityManager;

    @Test
    @DisplayName("save should save and return User")
    @Order(1)
    void save_shouldSaveAndReturnUser_WhenSuccessfull(){
        var user = User.builder().email("gabrieL@gomes.com").firstName("gabriel").lastName("gomes").build();

        var userSaved = userRepository.save(user);

        Assertions.assertThat(userSaved).hasNoNullFieldsOrProperties()
                .hasFieldOrPropertyWithValue("id", 2L);

    }

    @Test
    @DisplayName("should return user")
    @Order(2)
//    @Sql("/sql/init_sql_user")
    void findUserByFirstName_shouldReturnUser_WhenSuccessfull() {
        var user = userDataUtil.getUserToSave();

        var userToFind = userRepository.findUserByFirstName(user.getFirstName());

        Assertions.assertThat(userToFind)
                .isPresent();
    }

    @Test
    @DisplayName("should return user")
    @Order(3)
//    @Sql("/sql/init_sql_user")
    void findUserByEmail_shouldReturnUser_WhenSuccessfull() {
        var user = userDataUtil.getUserToSave();

        var userToFind = userRepository.findUserByEmail(user.getEmail());

        Assertions.assertThat(userToFind)
                .isPresent();
    }

    @Test
    @DisplayName("should return user")
    @Order(4)
//    @Sql("/sql/init_sql_user")
    void findUserByEmailAndIdNot_shouldReturnUser_WhenSuccessfull() {
        var user = userDataUtil.getUserToSave();

        var userToFind = userRepository.findUserByEmailAndIdNot(user.getEmail(), 2L);

        Assertions.assertThat(userToFind)
                .isPresent();
    }

    @Test
    @DisplayName("should return user")
    @Order(5)
//    @Sql("/sql/init_sql_user")
    void findById_shouldReturnUser_WhenSuccessfull() {
        var user = userDataUtil.getUserToSave();

        var usefind = userRepository.findUserByEmail(user.getEmail());
        System.out.println(usefind.get().getId());

        var userToFind = userRepository.findById(usefind.get().getId());

        Assertions.assertThat(userToFind)
                .isPresent();
    }

    @Test
    @DisplayName("should return optional empty")
    @Order(6)
    void findUserByFirstName_shouldReturnOptionalEmpty_whenNotFound() {

        var userToFind = userRepository.findUserByFirstName("nomeInexistente");

        Assertions.assertThat(userToFind)
                .isEmpty();
    }

    @Test
    @DisplayName("should return optional empty")
    @Order(7)
    void findUserByEmail_shouldReturnOptionalEmpty_whenNotFound() {

        var userToFind = userRepository.findUserByEmail("emailinexistente@gmail.com");

        Assertions.assertThat(userToFind)
                .isEmpty();
    }

    @Test
    @DisplayName("should return optional empty")
    @Order(8)
    void findUserByEmailAndIdNot_shouldReturnOptionalEmpty_whenNotFound() {

        var userToFind = userRepository.findUserByEmailAndIdNot("emailinexistente@gmail.com", 2L);

        Assertions.assertThat(userToFind)
                .isEmpty();
    }


    @Test
    @DisplayName("should return optional empty")
    @Order(9)
    void findById_shouldReturnOptionalEmpty_whenNotFound() {
        var user = userDataUtil.getUserToSave();

        var userToFind = userRepository.findById(user.getId());

        Assertions.assertThat(userToFind)
                .isEmpty();
    }

    @Test
    @DisplayName("should delete User")
    @Order(10)
    void delete_shouldDeleteUser_WhenSuccessfull() {
        var userSaved = userDataUtil.getUserToSave();

        userRepository.delete(userSaved);

        Assertions.assertThat(userRepository.findById(userSaved.getId())).isEmpty();
    }

}