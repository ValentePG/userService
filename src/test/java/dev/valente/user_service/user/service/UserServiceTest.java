package dev.valente.user_service.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.valente.user_service.brasilapiclient.response.CepGetErrorResponse;
import dev.valente.user_service.brasilapiclient.response.CepInnerErrorResponse;
import dev.valente.user_service.common.UserDataUtil;
import dev.valente.user_service.exception.EmailAlreadyExist;
import dev.valente.user_service.user.repository.UserRepositoryJPA;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepositoryJPA userRepository;

    private final UserDataUtil userDataUtil = new UserDataUtil();

    @Test
    @Order(1)
    @DisplayName("Should return list of all users")
    void findAll_shouldReturnAllUsers_whenSuccessfull() throws JsonProcessingException {

        mockList();

        var expectedResultList = userService.findAll();

        Assertions.assertThat(expectedResultList)
                .isEqualTo(userDataUtil.getListUsers());
    }

    @Test
    @Order(2)
    @DisplayName("Should return user by given id")
    void findByIdOrThrowNotFound_shouldReturnUser_whenSuccessfull() {

        var expectedUser = userDataUtil.getUserToFind();

        BDDMockito.when(userRepository.findById(expectedUser.getId())).thenReturn(Optional.of(expectedUser));

        var returnedUser = userService.findByIdOrThrowNotFound(expectedUser.getId());

        Assertions.assertThat(returnedUser)
                .isEqualTo(expectedUser);
    }

    @Test
    @Order(3)
    @DisplayName("Should return NOT FOUND")
    void findByIdOrThrowNotFound_shouldThrowNotFound_whenFailed() {

        var expectedUser = userDataUtil.getUserToFind();

        BDDMockito.when(userRepository.findById(expectedUser.getId())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> userService.findByIdOrThrowNotFound(expectedUser.getId()))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessage("404 NOT_FOUND \"User not found\"");
    }


    @Test
    @Order(4)
    @DisplayName("Should return user by given name")
    void findByFirstNameOrThrowNotFound_shouldReturnUser_whenSuccessfull() {

        var expectedUser = userDataUtil.getUserToFind();

        BDDMockito.when(userRepository.findUserByFirstName(expectedUser.getFirstName())).thenReturn(Optional.of(expectedUser));

        var returnedUser = userService.findByFirstNameOrThrowNotFound(expectedUser.getFirstName());

        Assertions.assertThat(returnedUser)
                .isEqualTo(expectedUser);
    }

    @Test
    @Order(5)
    @DisplayName("Should return NOT FOUND")
    void findByFirstNameOrThrowNotFound_shouldThrowNotFound_whenFailed() {

        var expectedUser = userDataUtil.getUserToFind();

        BDDMockito.when(userRepository.findUserByFirstName(expectedUser.getFirstName())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> userService.findByFirstNameOrThrowNotFound(expectedUser.getFirstName()))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessage("404 NOT_FOUND \"User not found\"");
    }

    @Test
    @Order(6)
    @DisplayName("Should return user by given email")
    void findByEmailOrThrowNotFound_shouldReturnUser_whenSuccessfull() {
        var expectedUser = userDataUtil.getUserToFind();

        BDDMockito.when(userRepository.findUserByEmail(expectedUser.getEmail())).thenReturn(Optional.of(expectedUser));

        var returnedUser = userService.findByEmailOrThrowNotFound(expectedUser.getEmail());

        Assertions.assertThat(returnedUser)
                .isEqualTo(expectedUser);
    }

    @Test
    @Order(7)
    @DisplayName("Should return NOT FOUND")
    void findByEmailOrThrowNotFound_shouldThrowNotFound_whenFailed() {

        var expectedUser = userDataUtil.getUserToFind();

        BDDMockito.when(userRepository.findUserByEmail(expectedUser.getEmail())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> userService.findByEmailOrThrowNotFound(expectedUser.getEmail()))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessage("404 NOT_FOUND \"User not found\"");
    }

    @Test
    @Order(8)
    @DisplayName("Should save user and return")
    void save_shouldSaveUser_whenSuccessfull() {

        var expectedUserToSave = userDataUtil.getUserToSave();

        BDDMockito.when(userRepository.findUserByEmail(expectedUserToSave.getEmail()))
                .thenReturn(Optional.empty());
        BDDMockito.when(userRepository.save(expectedUserToSave)).thenReturn(expectedUserToSave);

        var returnedUser = userService.save(expectedUserToSave);

        Assertions.assertThat(returnedUser)
                .isEqualTo(expectedUserToSave);
    }

    @Test
    @Order(9)
    @DisplayName("Should return email already exists")
    void save_shouldReturnEmailAlreadyExists_whenFailed() {

        var expectedUserToSave = userDataUtil.getUserToSave();

        BDDMockito.when(userRepository.findUserByEmail(expectedUserToSave.getEmail()))
                .thenReturn(Optional.of(expectedUserToSave));

        Assertions.assertThatThrownBy(() -> userService.save(expectedUserToSave))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessage("400 BAD_REQUEST \"Email %s already exists\"".formatted(expectedUserToSave.getEmail()));
    }


    @Test
    @Order(10)
    @DisplayName("Should replace user with new FirstName")
    void replace_shouldReplaceUserWithNewFirstName_whenSuccessfull() {
        var expectedUserToReplace = userDataUtil.getUserToReplace();
        var newUser = userDataUtil.getNewUserWithNewFirstName();
        expectedUserToReplace.setFirstName(newUser.getFirstName());

        BDDMockito.when(userRepository.findById(expectedUserToReplace.getId()))
                .thenReturn(Optional.of(expectedUserToReplace));

        BDDMockito.when(userRepository.save(newUser)).thenReturn(BDDMockito.any());

        Assertions.assertThatNoException()
                .isThrownBy(() -> userService.replace(newUser));

        Mockito.verify(userRepository, Mockito.times(1)).save(newUser);
        Mockito.verify(userRepository, Mockito.times(1)).findById(expectedUserToReplace.getId());

    }

    @Test
    @Order(11)
    @DisplayName("Should replace user with new Email")
    void replace_shouldReplaceUserWithNewEmail_whenSuccessfull() {
        var expectedUserToReplace = userDataUtil.getUserToReplace();
        var newUser = userDataUtil.getNewUserWithNewEmail();
        expectedUserToReplace.setEmail(newUser.getEmail());

        BDDMockito.when(userRepository.findById(expectedUserToReplace.getId()))
                .thenReturn(Optional.of(expectedUserToReplace));

        BDDMockito.when(userRepository.findUserByEmailAndIdNot(newUser.getEmail(),
                newUser.getId())).thenReturn(Optional.empty());

        BDDMockito.when(userRepository.save(newUser)).thenReturn(BDDMockito.any());

        Assertions.assertThatNoException()
                .isThrownBy(() -> userService.replace(newUser));

        Mockito.verify(userRepository, Mockito.times(1)).save(newUser);
        Mockito.verify(userRepository, Mockito.times(1)).findById(expectedUserToReplace.getId());

    }

    @Test
    @Order(12)
    @DisplayName("Should return email already exists")
    void replace_shouldReturnEmailAlreadyExists_whenFailed() {
        var expectedUserToReplace = userDataUtil.getUserToReplace();

        var newUser = userDataUtil.getNewUserWithNewEmail();

        BDDMockito.when(userRepository.findById(expectedUserToReplace.getId()))
                .thenReturn(Optional.of(expectedUserToReplace));

        BDDMockito.when(userRepository.findUserByEmailAndIdNot(newUser.getEmail(),
                newUser.getId())).thenReturn(Optional.of(expectedUserToReplace));

        Assertions.assertThatThrownBy(() -> userService.replace(newUser))
                .isInstanceOf(EmailAlreadyExist.class)
                .hasMessage("400 BAD_REQUEST \"Email %s already exists\""
                        .formatted(expectedUserToReplace.getEmail()));

        Mockito.verify(userRepository, Mockito.times(0)).save(newUser);
        Mockito.verify(userRepository, Mockito.times(1)).findById(expectedUserToReplace.getId());

    }


    @Test
    @Order(12)
    @DisplayName("Should replace user with new LastName")
    void replace_shouldReplaceUserWithNewLastName_whenSuccessfull() {
        var expectedUserToReplace = userDataUtil.getUserToReplace();
        var newUser = userDataUtil.getNewUserWithNewLastName();
        expectedUserToReplace.setLastName(newUser.getLastName());

        BDDMockito.when(userRepository.findById(expectedUserToReplace.getId()))
                .thenReturn(Optional.of(expectedUserToReplace));

        BDDMockito.when(userRepository.save(newUser)).thenReturn(BDDMockito.any());

        Assertions.assertThatNoException()
                .isThrownBy(() -> userService.replace(newUser));

        Mockito.verify(userRepository, Mockito.times(1)).save(newUser);
        Mockito.verify(userRepository, Mockito.times(1)).findById(expectedUserToReplace.getId());

    }


    @Test
    @Order(13)
    @DisplayName("Should return NOT FOUND")
    void replace_shouldThrowNotFound_whenFailed() {

        var expectedUserToReplace = userDataUtil.getUserToReplace();

        var newUser = userDataUtil.getNewUserWithNewEmail();

        BDDMockito.when(userRepository.findById(expectedUserToReplace.getId()))
                .thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> userService.replace(newUser))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessage("404 NOT_FOUND \"User not found\"");


        Mockito.verify(userRepository, Mockito.times(0)).save(newUser);
        Mockito.verify(userRepository, Mockito.times(1)).findById(expectedUserToReplace.getId());
    }

    @Test
    @Order(14)
    @DisplayName("Should delete user by given id")
    void delete_shouldDeleteUser_whenSuccessfull() {
        var expectedUserToDelete = userDataUtil.getUserToDelete();
        BDDMockito.when(userRepository.findById(expectedUserToDelete.getId())).thenReturn(Optional.of(expectedUserToDelete));


        Assertions.assertThatNoException()
                .isThrownBy(() -> userService.delete(expectedUserToDelete.getId()));

        Mockito.verify(userRepository, Mockito.times(1)).findById(expectedUserToDelete.getId());
    }

    @Test
    @Order(15)
    @DisplayName("Should return NOT FOUND")
    void delete_shouldThrowNotFound_whenFailed() {

        var expectedUserToDelete = userDataUtil.getUserToDelete();
        BDDMockito.when(userRepository.findById(expectedUserToDelete.getId())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> userService.delete(expectedUserToDelete.getId()))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessage("404 NOT_FOUND \"User not found\"");

        Mockito.verify(userRepository, Mockito.times(1)).findById(expectedUserToDelete.getId());
    }


    private void mockList() {
        BDDMockito.when(userRepository.findAll()).thenReturn(userDataUtil.getListUsers());
    }
}