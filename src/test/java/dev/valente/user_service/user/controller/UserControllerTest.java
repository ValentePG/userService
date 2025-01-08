package dev.valente.user_service.user.controller;

import dev.valente.user_service.user.common.FileUtil;
import dev.valente.user_service.user.common.UserDataUtil;
import dev.valente.user_service.user.dto.httprequest.post.UserPostRequest;
import dev.valente.user_service.user.repository.UserData;
import dev.valente.user_service.user.repository.UserRepositoryJPA;
import dev.valente.user_service.user.service.UserMapperService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.validation.Errors;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@WebMvcTest(UserController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ComponentScan("dev.valente.user_service")
class UserControllerTest {

    private final String URL = "/v1/users";

    @InjectMocks
    private UserController userController;

    @MockBean
    private UserRepositoryJPA userRepository;

    @SpyBean
    private UserMapperService userMapperService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FileUtil fileUtil;

    @Autowired
    private UserDataUtil userDataUtil;

    @BeforeEach
    void setUp() {
        mockList();
    }

    @Test
    @DisplayName("GET v1/users should return list of all users")
    @Order(1)
    void findAll_shouldReturnListOfUsers_whenSuccessfull() throws Exception {

        var pathResponse = "/userservice/get/get_findallusers_200.json";

        var expectedList = fileUtil.readFile(pathResponse);

        mockMvc.perform(MockMvcRequestBuilders.get(URL))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedList));

    }

    @Test
    @DisplayName("GET v1/users/{existentId} should return a user")
    @Order(2)
    void findById_shouldReturnUser_whenSuccessfull() throws Exception {
        var pathResponse = "/userservice/get/get_findbyid_200.json";

        var existentUser = userDataUtil.getUserToFind();

        BDDMockito.when(userRepository.findById(existentUser.getId())).thenReturn(Optional.of(existentUser));

        var expectedUserFromFile = fileUtil.readFile(pathResponse);

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}", existentUser.getId()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedUserFromFile));
    }

    @Test
    @DisplayName("GET v1/users/{inexistentId} should return NOT FOUND")
    @Order(3)
    void findById_shouldReturnNotFound_whenFailed() throws Exception {

        var response = fileUtil.readFile("/userservice/get/get_findbyid-inexistentid_404.json");

        var inexistentId = 50L;

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}", inexistentId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("GET v1/users/find?email=existentEmail should return a user")
    @Order(4)
    void findWithParamsWithEmail_shouldReturnUser_whenSuccessfull() throws Exception {
        var pathResponse = "/userservice/get/get_findbyemail_200.json";

        var expectedUserFromFile = fileUtil.readFile(pathResponse);

        var existentUser = userDataUtil.getUserToFind();

        BDDMockito.when(userRepository.findUserByEmail(existentUser.getEmail())).thenReturn(Optional.of(existentUser));

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/find")
                        .param("email", existentUser.getEmail()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedUserFromFile));

    }

    @Test
    @DisplayName("GET v1/users/find?email=inexistentEmail should return NOT FOUND")
    @Order(5)
    void findWithParamsWithNewEmail_shouldReturnNotFound_whenFailed() throws Exception {

        var response = fileUtil.readFile("/userservice/get/get_findbyemail-inexistentemail_404.json");

        var inexistentEmail = "inexistent@gmail.com";

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/find")
                        .param("email", inexistentEmail))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("GET v1/users/find?firstName=existentFirstName should return a user")
    @Order(6)
    void findWithParamsWithFirstName_shouldReturnUser_whenSuccessfull() throws Exception {
        var pathResponse = "/userservice/get/get_findbyfirstname_200.json";

        var expectedUserFromFile = fileUtil.readFile(pathResponse);

        var existentUser = userDataUtil.getUserToFind();

        BDDMockito.when(userRepository.findUserByFirstName(existentUser.getFirstName()))
                .thenReturn(Optional.of(existentUser));


        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/find")
                        .param("firstName", existentUser.getFirstName()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedUserFromFile));

    }

    @Test
    @DisplayName("GET v1/users/find?firstName=inexistentFirstName should return NOT FOUND")
    @Order(7)
    void findWithParamsWithFirstName_shouldReturnNotFound_whenFailed() throws Exception {

        var response = fileUtil.readFile("/userservice/get/get_findbyfirstname-inexistfirstname_404.json");

        var inexistentFirstName = "Inexistent";

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/find")
                        .param("firstName", inexistentFirstName))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("POST v1/users should save and return a user")
    @Order(8)
    void save_shouldSaveAndReturnUser_whenSuccessfull() throws Exception {
        var pathResponse = "/userservice/post/post_createduser_201.json";
        var pathRequest = "/userservice/post/post_createuser_200.json";

        var expectedUserSaved = userDataUtil.getUserToSave();

        BDDMockito.when(userMapperService
                        .userPostRequestToUser(ArgumentMatchers.any(UserPostRequest.class)))
                .thenReturn(expectedUserSaved);

        BDDMockito.when(userRepository.save(expectedUserSaved)).thenReturn(expectedUserSaved);


        var response = fileUtil.readFile(pathResponse);

        var request = fileUtil.readFile(pathRequest);

        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json(response));

    }

    @ParameterizedTest
    @MethodSource("postParameterizedTest")
    @DisplayName("POST v1/users should return BAD REQUEST")
    @Order(9)
    void save_shouldReturnBadRequest_whenFailed(String fileName, List<String> Errors) throws Exception {

        var request = fileUtil.readFile(fileName);

        var resultMvc = mockMvc.perform(MockMvcRequestBuilders.post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();

        var resolvedException = resultMvc.getResolvedException();

        Assertions.assertThat(resolvedException).isNotNull();

        Assertions.assertThat(resolvedException.getMessage()).contains(Errors);

    }

    private static Stream<Arguments> postParameterizedTest() {

        var blankFirstName = "FirstName não pode estar em branco";
        var blankLastName = "LastName não pode estar em branco";
        var blankEmail = "Email não pode estar em branco";
        var errorBlankList = List.of(blankFirstName, blankLastName, blankEmail);


        return Stream.of(
                Arguments.of("/userservice/post/post_createuser-empty-values_400.json", errorBlankList),
                Arguments.of("/userservice/post/post_createuser-blank-values_400.json", errorBlankList)
        );
    }

    @ParameterizedTest
    @MethodSource("putParametrizedTest")
    @DisplayName("PUT v1/users payload with existentId should replace a user with valid email, firstName and Lastname")
    @Order(10)
    void replaceWithValidPayload_shouldReplaceUserWithValidPayload_whenSuccessfull(String fileName) throws Exception {

        var request = fileUtil.readFile(fileName);

        var existentUser = userDataUtil.getThirdUser();

        BDDMockito.when(userRepository.findById(existentUser.getId())).thenReturn(Optional.of(existentUser));

        mockMvc.perform(MockMvcRequestBuilders.put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    private static Stream<Arguments> putParametrizedTest() {
        var withNewEmail = "/userservice/put/put_replacewithnewemail_200.json";
        var withNewFirstName = "/userservice/put/put_replacewithnewfirstname_200.json";
        var withNewLastName = "/userservice/put/put_replacewithnewlastname_200.json";

        return Stream.of(
                Arguments.of(withNewEmail),
                Arguments.of(withNewFirstName),
                Arguments.of(withNewLastName)
        );
    }

    @ParameterizedTest
    @MethodSource("putWithInvalidFields")
    @DisplayName("PUT v1/users payload should return BAD REQUEST with invalid fields")
    @Order(11)
    void replaceWithInvalidPayload_shouldReturnBadRequest_whenFailed(String fileName, String error) throws Exception {
        var request = fileUtil.readFile(fileName);

        var resultMvc = mockMvc.perform(MockMvcRequestBuilders.put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();

        var resolvedException = resultMvc.getResolvedException();

        Assertions.assertThat(resolvedException).isNotNull();

        Assertions.assertThat(resolvedException.getMessage()).contains(error);
    }

    private static Stream<Arguments> putWithInvalidFields() {
        var invalidEmail = "/userservice/put/put_replacewithinvalidemail_400.json";
        var blankEmail = "/userservice/put/put_replacewithblank-empty-email_400.json";
        var blankFirstName = "/userservice/put/put_replacewithblank-empty-firstname_400.json";
        var blankLastName = "/userservice/put/put_replacewithblank-empty-lastname_400.json";

        var invalidEmailError = "Email inválido";
        var badRequestError = "Pelo menos um campo precisa estar preenchido corretamente";

        return Stream.of(
                Arguments.of(invalidEmail,invalidEmailError),
                Arguments.of(blankEmail, badRequestError),
                Arguments.of(blankFirstName, badRequestError),
                Arguments.of(blankLastName, badRequestError)
        );
    }

    @Test
    @DisplayName("PUT v1/users payload with inexistentId should return NOT FOUND")
    @Order(12)
    void replace_shouldReturnNotFound_whenFailed() throws Exception {
        var pathRequest = "/userservice/put/put_replacewithinexistentid_404.json";

        var pathResponse = "/userservice/put/put_replacenotfound_404.json";

        var response = fileUtil.readFile(pathResponse);

        var request = fileUtil.readFile(pathRequest);

        mockMvc.perform(MockMvcRequestBuilders.put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(response));

    }

    @Test
    @DisplayName("DELETE v1/users/{existentId} should delete user")
    @Order(13)
    void deleteById_shouldDeleteUser_whenSuccessfull() throws Exception {

        var existentUser = userDataUtil.getUserToDelete();

        BDDMockito.when(userRepository.findById(existentUser.getId())).thenReturn(Optional.of(existentUser));
        BDDMockito.doNothing().when(userRepository).delete(existentUser);

        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/{id}", existentUser.getId()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());

    }

    @Test
    @DisplayName("DELETE v1/users/{inexistentId} should return NOT FOUND")
    @Order(14)
    void deleteById_shouldReturnNotFound_whenFailed() throws Exception {

        var pathResponse = "/userservice/delete/delete_idnotfound_404.json";

        var response = fileUtil.readFile(pathResponse);

        var inexistentId = 50L;

        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/{id}", inexistentId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(response));

    }

    private void mockList() {
        BDDMockito.when(userRepository.findAll()).thenReturn(userDataUtil.getListUsers());
    }


}