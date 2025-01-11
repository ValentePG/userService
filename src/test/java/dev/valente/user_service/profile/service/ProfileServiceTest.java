package dev.valente.user_service.profile.service;

import dev.valente.user_service.common.ProfileDataUtil;
import dev.valente.user_service.exception.NotFoundException;
import dev.valente.user_service.exception.UserNameAlreadyExists;
import dev.valente.user_service.profile.repository.ProfileRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.Optional;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
@ComponentScan(basePackages = {"dev.valente.user_service.profile","dev.valente.user_service.common"})
class ProfileServiceTest {

    @InjectMocks
    private ProfileService profileService;

    @Mock
    private ProfileRepository profileRepository;

    private ProfileDataUtil profileDataUtil = new ProfileDataUtil();

    @Test
    @DisplayName("findAll with name null should return list of all profiles")
    @Order(1)
    void findAll_shouldReturnListOfAllProfiles_whenNameIsNull() {
        mockList();

        var response = profileService.findAll(null);

        Assertions.assertThat(response)
                .isNotEmpty()
                .hasSameElementsAs(profileDataUtil.getListProfile());
    }

    @Test
    @DisplayName("findAll with name should try to find profiles by name or else return empty list")
    @Order(2)
    void findAll_shouldReturnListProfileByNameOrEmptyList_whenNameIsNotNull() {
        var existentProfile = profileDataUtil.getFirst();

        BDDMockito.when(profileRepository.findAllByName(existentProfile.getName())).thenReturn(Collections.singletonList(existentProfile));

        var response = profileService.findAll(existentProfile.getName());

        Assertions.assertThat(response).isNotEmpty();
    }

    @Test
    @DisplayName("findAllPaginated should return page of profiles")
    @Order(3)
    void findAllPaginated_shouldReturnPageOfProfiles_whenSucessful() {

        var pageRequest = PageRequest.of(0, profileDataUtil.getListProfile().size());

        var pageProfile = new PageImpl<>(profileDataUtil.getListProfile(), pageRequest, 1);

        BDDMockito.when(profileRepository.findAll(pageRequest)).thenReturn(pageProfile);

        var pageResponse = profileService.findAllPaginated(pageRequest);

        Assertions.assertThat(pageResponse).hasSameElementsAs(profileDataUtil.getListProfile());
    }

    @Test
    @DisplayName("findByIdOrThrowNotFound should return profile when exists")
    @Order(4)
    void findByIdOrThrowNotFound_shouldReturnProfile_whenExists() {

        var existentProfile = profileDataUtil.getFirst();

        BDDMockito.when(profileRepository.findById(existentProfile.getId())).thenReturn(Optional.of(existentProfile));

        var response = profileService.findByIdOrThrowNotFound(existentProfile.getId());

        Assertions.assertThat(response).isIn(profileDataUtil.getListProfile())
                .hasNoNullFieldsOrProperties()
                .matches(p -> p.getId().equals(existentProfile.getId()));
    }

    @Test
    @DisplayName("findByIdOrThrowNotFound should return NOT FOUND")
    @Order(5)
    void findByIdOrThrowNotFound_shouldThrowNotFound_whenNotFound() {

        var profileId = profileDataUtil.getFirst().getId();

        BDDMockito.when(profileRepository.findById(profileId)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> profileService.findByIdOrThrowNotFound(profileId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("404 NOT_FOUND \"Profile not found!\"");
    }

    @Test
    @DisplayName("createProfile should save and return Profile")
    @Order(6)
    void createProfile_shouldSaveProfile_whenProfileDoesNotExist() {
        var profileToSave = profileDataUtil.getUserToSave();
        var savedProfile = profileDataUtil.getUserAfterSave();
        profileDataUtil.getListProfile().add(savedProfile);

        BDDMockito.when(profileRepository.save(profileToSave)).thenReturn(savedProfile);
        BDDMockito.when(profileRepository.findByName(profileToSave.getName())).thenReturn(Optional.empty());

        var response = profileService.createProfile(profileToSave);

        Assertions.assertThat(response).isIn(profileDataUtil.getListProfile())
                .isEqualTo(savedProfile);
    }

    @Test
    @DisplayName("createProfile should return NOT FOUND")
    @Order(7)
    void createProfile_shouldReturnNameAlreadyExist_whenProfileExist() {
        var profileToSave = profileDataUtil.getUserToSave();

        var savedProfile = profileDataUtil.getUserAfterSave();

        BDDMockito.when(profileRepository.findByName(profileToSave.getName())).thenReturn(Optional.of(savedProfile));

        Assertions.assertThatThrownBy(() -> profileService.createProfile(profileToSave))
                .isInstanceOf(UserNameAlreadyExists.class)
                .hasMessage("400 BAD_REQUEST \"Profile with name sone already exists!\"");
    }


    private void mockList(){
        BDDMockito.when(profileRepository.findAll()).thenReturn(profileDataUtil.getListProfile());
    }
}