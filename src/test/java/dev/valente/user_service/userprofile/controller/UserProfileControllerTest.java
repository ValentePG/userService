package dev.valente.user_service.userprofile.controller;

import dev.valente.user_service.common.FileUtil;
import dev.valente.user_service.common.UserProfileDataUtil;
import dev.valente.user_service.userprofile.mapper.UserProfileMapper;
import dev.valente.user_service.userprofile.repository.UserProfileRepository;
import dev.valente.user_service.userprofile.service.UserProfileService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserProfileController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
@ComponentScan(basePackages = {"dev.valente.user_service.userprofile", "dev.valente.user_service.common"})
class UserProfileControllerTest {

    private final String URL = "/v1/user-profiles";

    @InjectMocks
    private UserProfileController userProfileController;

    @MockitoBean
    private UserProfileService userProfileService;

    @MockitoBean
    private UserProfileRepository userProfileRepository;

    @MockitoSpyBean
    private UserProfileMapper userProfileMapper;

    @Autowired
    private FileUtil fileUtil;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserProfileDataUtil userProfileDataUtil;

    private void mockList() {
        BDDMockito.when(userProfileRepository.findAll()).thenReturn(userProfileDataUtil.getListUserProfile());
    }
}