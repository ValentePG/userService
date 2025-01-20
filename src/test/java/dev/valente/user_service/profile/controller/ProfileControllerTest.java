package dev.valente.user_service.profile.controller;

import dev.valente.user_service.common.FileUtil;
import dev.valente.user_service.common.ProfileDataUtil;
import dev.valente.user_service.profile.dto.post.ProfilePostRequest;
import dev.valente.user_service.profile.mapper.ProfileMapper;
import dev.valente.user_service.profile.repository.ProfileRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@WebMvcTest(controllers = ProfileController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
@ComponentScan(basePackages = {"dev.valente.user_service.profile", "dev.valente.user_service.common", "dev.valente.user_service.config"})
@WithMockUser
class ProfileControllerTest {

    private final String URL = "/v1/profiles";

    @InjectMocks
    private ProfileController profileController;

    @MockBean
    private ProfileRepository profileRepository;

    @SpyBean
    private ProfileMapper profileMapper;

    @Autowired
    private FileUtil fileUtil;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProfileDataUtil profileDataUtil;

    @Test
    @DisplayName("GET v1/profiles/paginated should return page of Profiles")
    @Order(1)
    void findAllPaginated_shouldReturnPageOfProfiles() throws Exception {

        var pageRequest = PageRequest.of(0, profileDataUtil.getListProfile().size());
        var pageProfile = new PageImpl<>(profileDataUtil.getListProfile(), pageRequest, profileDataUtil.getListProfile().size());

        var response = fileUtil.readFile("/profile/get/get_findallpaginated_200.json");


        BDDMockito.when(profileRepository.findAll(pageRequest)).thenReturn(pageProfile);

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/paginated")
                        .param("page", "0")
                        .param("size", String.valueOf(profileDataUtil.getListProfile().size())))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));


    }

    @Test
    @DisplayName("GET v1/profiles should return a list of profile when satisfy parameter name")
    @Order(2)
    void findAll_shouldReturnListOfProfile_withParameter() throws Exception {
        var response = fileUtil.readFile("/profile/get/get_findallparameterized_200.json");
        var expectedUserToFind = profileDataUtil.getFirst();

        BDDMockito.when(profileRepository.findAllByName(expectedUserToFind.getName()))
                .thenReturn(Collections.singletonList(expectedUserToFind));

        mockMvc.perform(MockMvcRequestBuilders.get(URL)
                        .param("name", expectedUserToFind.getName()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("GET v1/profiles should return a list of all profiles without parameters")
    @Order(3)
    void findAll_shouldReturnListOfAllProfiles_withoutParameters() throws Exception {
        mockList();
        var response = fileUtil.readFile("/profile/get/get_findall_200.json");

        mockMvc.perform(MockMvcRequestBuilders.get(URL))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("GET v1/profiles/{id} should return NOT FOUND when profiles does not exists")
    @Order(4)
    void findById_shouldReturnNotFoundException_whenProfileDoesNotExists() throws Exception {
        var expectedProfile = profileDataUtil.getFirst();

        var response = fileUtil.readFile("/profile/get/get_findbyid-inexistentid_404.json");

        BDDMockito.when(profileRepository.findById(expectedProfile.getId())).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}", expectedProfile.getId()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(response));

    }

    @Test
    @DisplayName("GET v1/profiles/{id} should return profile when exists")
    @Order(5)
    void findById_shouldReturnProfile_whenExists() throws Exception {
        var expectedProfile = profileDataUtil.getFirst();

        var response = fileUtil.readFile("/profile/get/get_findbyid_200.json");

        BDDMockito.when(profileRepository.findById(expectedProfile.getId())).thenReturn(Optional.of(expectedProfile));

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}", expectedProfile.getId()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));

    }

    @Test
    @DisplayName("POST v1/profiles should return userNameAlreadyExists")
    @Order(6)
    void createProfile_shouldReturnNameAlreadyExists_whenNameAlreadyExists() throws Exception {
        var existentProfile = profileDataUtil.getFirst();

        var request = fileUtil.readFile("/profile/post/post_requestusernamealreadyexists_400.json");
        var response = fileUtil.readFile("/profile/post/post_responseusernamealreadyexistsexception_400.json");

        BDDMockito.when(profileRepository.findByName(existentProfile.getName())).thenReturn(Optional.of(existentProfile));

        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(response));

    }

    @Test
    @DisplayName("POST v1/profiles should save user with valid data")
    @Order(7)
    void createProfile_shouldSaveUser_withValidData() throws Exception {
        var profileToSave = profileDataUtil.getUserToSave();
        var savedProfile = profileDataUtil.getUserAfterSave();

        BDDMockito.when(profileMapper.profilePostRequestToProfile(BDDMockito.any(ProfilePostRequest.class)))
                .thenReturn(profileToSave);

        BDDMockito.when(profileRepository.save(profileToSave)).thenReturn(savedProfile);

        var request = fileUtil.readFile("/profile/post/post_createprofilewithvaliddata_200.json");

        var response = fileUtil.readFile("/profile/post/post_createdprofile_201.json");

        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json(response));

    }

    @ParameterizedTest
    @MethodSource("postParameterizedTest")
    @DisplayName("POST v1/profiles should return BAD REQUEST")
    @Order(8)
    void createProfile_shouldReturnBadRequest_withInvalidData(String file, List<String> errors) throws Exception {

        var request = fileUtil.readFile(file);

        var mockMvcResult = mockMvc.perform(MockMvcRequestBuilders.post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();

        var resolvedException = mockMvcResult.getResolvedException();

        Assertions.assertThat(resolvedException).isNotNull();

        Assertions.assertThat(resolvedException.getMessage()).contains(errors);
    }

    private static Stream<Arguments> postParameterizedTest() throws IOException {
        var blankData = "/profile/post/post_createprofilewithdata-blank_400.json";
        var emptyData = "/profile/post/post_createprofilewithdata-empty_400.json";
        var nullData = "/profile/post/post_createprofilewithdata-null_400.json";

        var nameError = "O campo nome não pode estar vazio";
        var descriptionError = "O campo descrição não pode estar vazio";
        var listOfErrors = List.of(nameError, descriptionError);

        return Stream.of(
                Arguments.of(blankData, listOfErrors),
                Arguments.of(emptyData, listOfErrors),
                Arguments.of(nullData, listOfErrors)
        );
    }

    private void mockList() {
        BDDMockito.when(profileRepository.findAll()).thenReturn(profileDataUtil.getListProfile());
    }
}