package dev.valente.user_service.profile.controller;

import dev.valente.user_service.common.FileUtil;
import dev.valente.user_service.common.ProfileDataUtil;
import dev.valente.user_service.profile.dto.post.ProfilePostRequest;
import dev.valente.user_service.profile.mapper.ProfileMapper;
import dev.valente.user_service.profile.repository.ProfileRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
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

import java.util.List;
import java.util.Optional;

@WebMvcTest(controllers = ProfileController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
@ComponentScan(basePackages = {"dev.valente.user_service.profile","dev.valente.user_service.common"})
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
    @DisplayName("POST v1/profiles should return userNameAlreadyExists")
    @Order(8)
    void createProfile_shouldReturnNameAlreadyExists_whenNameAlreadyExists() throws Exception {
        var existentProfile = profileDataUtil.getFirst();

        var request = fileUtil.readFile("/profile/post/post_requestusernamealreadyexists_400.json");
        var response = fileUtil.readFile("/profile/post/post_responseusernamealreadyexists_400.json");

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
    @Order(9)
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

    @Test
    @DisplayName("POST v1/profiles should return BAD REQUEST")
    @Order(10)
    void createProfile_shouldReturnBadRequest_withInvalidData() throws Exception {

        var request = fileUtil.readFile("/profile/post/post_createprofilewithinvaliddata_400.json");
        var listOfErrors = List.of("O campo nome não pode estar vazio","O campo descrição não pode estar vazio");

        var mockMvcResult = mockMvc.perform(MockMvcRequestBuilders.post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();

        var resolvedException = mockMvcResult.getResolvedException();

        Assertions.assertThat(resolvedException).isNotNull();

        Assertions.assertThat(resolvedException.getMessage()).contains(listOfErrors);
    }

    private void mockList(){
        BDDMockito.when(profileRepository.findAll()).thenReturn(profileDataUtil.getListProfile());
    }
}