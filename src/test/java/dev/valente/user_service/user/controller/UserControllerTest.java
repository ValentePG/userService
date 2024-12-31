package dev.valente.user_service.user.controller;

import dev.valente.user_service.user.common.FileUtil;
import dev.valente.user_service.user.common.UserDataUtil;
import dev.valente.user_service.user.dto.httprequest.post.UserPostRequest;
import dev.valente.user_service.user.repository.UserData;
import dev.valente.user_service.user.service.UserMapperService;
import org.junit.jupiter.api.*;
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

@WebMvcTest(UserController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ComponentScan("dev.valente.user_service")
class UserControllerTest {

    private final String URL = "/v1/users";

    @InjectMocks
    private UserController userController;

    @MockBean
    private UserData userData;

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
    @DisplayName("GET v1/users/{id} should return a user")
    @Order(2)
    void findById_shouldReturnUser_whenSuccessfull() throws Exception {
        var pathResponse = "/userservice/get/get_findbyid_200.json";

        var idFromExpectedUser = userDataUtil.getUserToFind().getId();

        var expectedUserFromFile = fileUtil.readFile(pathResponse);

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}", idFromExpectedUser))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedUserFromFile));
    }

    @Test
    @DisplayName("GET v1/users/{id} should return NOT FOUND")
    @Order(3)
    void findById_shouldReturnNotFound_whenFailed() throws Exception {

        var id = 50L;

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}", id))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.status().reason("User not found"));
    }

    @Test
    @DisplayName("GET v1/users/find?email=jonydoe@gmail.com should return a user")
    @Order(4)
    void findWithParamsWithEmail_shouldReturnUser_whenSuccessfull() throws Exception {
        var pathResponse = "/userservice/get/get_findbyemail_200.json";

        var expectedUserFromFile = fileUtil.readFile(pathResponse);

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/find")
                        .param("email", "jonydoe@gmail.com"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedUserFromFile));

    }

    @Test
    @DisplayName("GET v1/users/find?email=inexistent@gmail.com should return NOT FOUND")
    @Order(5)
    void findWithParamsWithNewEmail_shouldReturnNotFound_whenFailed() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/find")
                        .param("email", "inexistent@gmail.com"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.status().reason("User not found"));
    }

    @Test
    @DisplayName("GET v1/users/find?firstName=Johny should return a user")
    @Order(6)
    void findWithParamsWithFirstName_shouldReturnUser_whenSuccessfull() throws Exception {
        var pathResponse = "/userservice/get/get_findbyfirstname_200.json";

        var expectedUserFromFile = fileUtil.readFile(pathResponse);

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/find")
                        .param("firstName", "Johny"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedUserFromFile));
    }

    @Test
    @DisplayName("GET v1/users/find?firstName=Inexistent should return NOT FOUND")
    @Order(7)
    void findWithParamsWithFirstName_shouldReturnNotFound_whenFailed() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/find")
                        .param("firstName", "Inexistent"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.status().reason("User not found"));
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

        var response = fileUtil.readFile(pathResponse);

        var request = fileUtil.readFile(pathRequest);

        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json(response));

    }

    @Test
    @DisplayName("PUT v1/users should replace a user with new email")
    @Order(9)
    void replaceWithNewEmail_shouldReplaceUserWithNewEmail_whenSuccessfull() throws Exception {
        var pathRequest = "/userservice/put/put_replacewithnewemail_200.json";

        var request = fileUtil.readFile(pathRequest);

        mockMvc.perform(MockMvcRequestBuilders.put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("PUT v1/users should replace a user with new firstName")
    @Order(10)
    void replaceWithNewFirstName_shouldReplaceUserWithNewFirstName_whenSuccessfull() throws Exception {
        var pathRequest = "/userservice/put/put_replacewithnewfirstname_200.json";

        var request = fileUtil.readFile(pathRequest);

        mockMvc.perform(MockMvcRequestBuilders.put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("PUT v1/users should replace a user with new lastName")
    @Order(11)
    void replaceWithNewLastName_shouldReplaceUserWithNewLastName_whenSuccessfull() throws Exception {
        var pathRequest = "/userservice/put/put_replacewithnewlastname_200.json";

        var request = fileUtil.readFile(pathRequest);

        mockMvc.perform(MockMvcRequestBuilders.put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());

    }

    @Test
    @DisplayName("PUT v1/users should return NOT FOUND")
    @Order(12)
    void replace_shouldReturnNotFound_whenFailed() throws Exception {
        var pathRequest = "/userservice/put/put_replacewithinexistentid_404.json";

        var request = fileUtil.readFile(pathRequest);

        mockMvc.perform(MockMvcRequestBuilders.put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.status().reason("User not found"));

    }

    @Test
    @DisplayName("DELETE v1/users/{id} should delete user")
    @Order(13)
    void deleteById_shouldDeleteUser_whenSuccessfull() throws Exception {

        var idFromexpectedToExclude = userDataUtil.getUserToDelete().getId();

        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/{id}", idFromexpectedToExclude))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());

    }

    @Test
    @DisplayName("DELETE v1/users/{id} should return NOT FOUND")
    @Order(14)
    void deleteById_shouldReturnNotFound_whenFailed() throws Exception {

        var id = 50L;

        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/{id}", id))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.status().reason("User not found"));

    }

    private void mockList(){
        BDDMockito.when(userData.getListUsers()).thenReturn(userDataUtil.getListUsers());
    }


}