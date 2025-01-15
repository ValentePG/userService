package dev.valente.user_service.userprofile.service;

import dev.valente.user_service.common.ProfileDataUtil;
import dev.valente.user_service.common.UserDataUtil;
import dev.valente.user_service.common.UserProfileDataUtil;
import dev.valente.user_service.domain.UserProfile;
import dev.valente.user_service.userprofile.repository.UserProfileRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

    @InjectMocks
    private UserProfileService userProfileService;

    @Mock
    private UserProfileRepository userProfileRepository;

    @InjectMocks
    private UserProfileDataUtil userProfileDataUtil;

    @Spy
    private UserDataUtil userDataUtil;

    @Spy
    private ProfileDataUtil profileDataUtil;

    @Test
    @DisplayName("findAll should return list of all userProfiles")
    @Order(1)
    void findAll_shouldReturnAllUserProfiles_whenSuccessfull() {
        mockList();

        var response = userProfileService.findAll();

        Assertions.assertThat(response).isNotEmpty()
                .hasSameElementsAs(userProfileDataUtil.getListUserProfile());

        response.forEach(u -> Assertions.assertThat(u).hasNoNullFieldsOrProperties());

    }

    @Test
    @DisplayName("findAllByProfile_id should return users with given profile_id")
    @Order(2)
    void findAllByProfileId_shouldReturnUserWithGivenProfileId_whenSuccessfull() {
        var existentId = userProfileDataUtil.getFirst().getId();

        var filteredListUserProfile = userProfileDataUtil.getListUserProfile().stream()
                .filter(u -> Objects.equals(u.getProfile().getId(), existentId))
                .toList();

        var listUsers = filteredListUserProfile.stream().map(UserProfile::getUser).toList();

        BDDMockito.when(userProfileRepository.findAllByProfile_Id(existentId))
                .thenReturn(filteredListUserProfile);

        var response = userProfileService.findAllByProfileId(existentId);

        Assertions.assertThat(response)
                .hasSameElementsAs(listUsers)
                .isNotEmpty();

        response.forEach(u -> Assertions.assertThat(u).hasNoNullFieldsOrProperties());
    }

    @Test
    @DisplayName("findAllUserByProfileId should return users with given profile_id")
    @Order(3)
    void findAllUserByProfileId_shouldReturnUserWithGivenProfileId_whenSuccessfull() {
        var existentId = userProfileDataUtil.getFirst().getId();

        var filteredListUserProfile = userProfileDataUtil.getListUserProfile().stream()
                .filter(u -> Objects.equals(u.getProfile().getId(), existentId))
                .toList();

        var listUsers = filteredListUserProfile.stream().map(UserProfile::getUser).toList();

        BDDMockito.when(userProfileRepository.findAllUsersByProfileId(existentId))
                .thenReturn(listUsers);

        var response = userProfileService.findAllUserByProfileId(existentId);

        Assertions.assertThat(response)
                .hasSameElementsAs(listUsers)
                .isNotEmpty();

        response.forEach(u -> Assertions.assertThat(u).hasNoNullFieldsOrProperties());
    }


    private void mockList() {
        BDDMockito.when(userProfileRepository.findAll()).thenReturn(userProfileDataUtil.getListUserProfile());
    }
}