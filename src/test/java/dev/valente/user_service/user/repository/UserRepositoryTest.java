package dev.valente.user_service.user.repository;

import dev.valente.user_service.user.common.UserDataUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserRepositoryTest {

    @InjectMocks
    private UserRepository userRepository;

    @Mock
    private UserData userData;

    private final UserDataUtil userDataUtil = new UserDataUtil();

    @BeforeEach
    void setUp() {
        mockList();
    }

    @Test
    @Order(1)
    @DisplayName("Should return a list of all Users")
    void findAll_shouldReturnListOfAllUsers_whenSuccessfull() {

        var expectedListOfUsers = userRepository.findAll();

        Assertions.assertThat(expectedListOfUsers)
                .isEqualTo(userDataUtil.getListUsers());
    }

    @Test
    @Order(2)
    @DisplayName("Should return a User by given email")
    void findByEmail_shouldReturnUser_whenSuccessfull() {

        var expectedUser = userDataUtil.getUserToFind();

        var returnedUser = userRepository.findByEmail(expectedUser.getEmail());

        Assertions.assertThat(returnedUser)
                .get()
                .isEqualTo(expectedUser);
    }

    @Test
    @Order(3)
    @DisplayName("Should return a User by given id")
    void findById_shouldReturnUser_whenSuccessfull() {
        var expectedUser = userDataUtil.getUserToFind();

        var returnedUser = userRepository.findById(expectedUser.getId());

        Assertions.assertThat(returnedUser)
                .get()
                .isEqualTo(expectedUser);
    }

    @Test
    @Order(4)
    @DisplayName("Should return a User by given firstName")
    void findByFirstName_shouldReturnUser_whenSuccessfull() {
        var expectedUser = userDataUtil.getUserToFind();

        var returnedUser = userRepository.findByFirstName(expectedUser.getFirstName());

        Assertions.assertThat(returnedUser)
                .get()
                .isEqualTo(expectedUser);
    }

    @Test
    @Order(5)
    @DisplayName("Should save and return User when successfull")
    void save_shouldSaveAndReturnUser_whenSuccessfull() {

        var userToSave = userDataUtil.getUserToSave();

        var savedUser = userRepository.save(userToSave);

        Assertions.assertThat(savedUser)
                .isIn(userDataUtil.getListUsers())
                .isEqualTo(userToSave)
                .hasNoNullFieldsOrProperties();
    }

    @Test
    @Order(6)
    @DisplayName("Should replace User when successfull")
    void replace_shouldReplaceUser_whenSuccessfull() {
        var oldUser = userDataUtil.getUserToReplace();

        var newUser = userDataUtil.getNewUserWithNewEmail();

        userRepository.replace(oldUser, newUser);

        Assertions.assertThat(newUser)
                .isIn(userDataUtil.getListUsers())
                .hasNoNullFieldsOrProperties();

        Assertions.assertThat(oldUser)
                .isNotIn(userDataUtil.getListUsers());
    }


    @Test
    @Order(7)
    @DisplayName("Should remove a User")
    void delete_shouldRemoveUser_whenSuccessfull() {
        var expectedUserToExclude = userDataUtil.getUserToFind();

        userRepository.delete(expectedUserToExclude);

        Assertions.assertThat(expectedUserToExclude)
                .isNotIn(userDataUtil.getListUsers());
    }

    private void mockList() {
        BDDMockito.when(userData.getListUsers()).thenReturn(userDataUtil.getListUsers());
    }
}