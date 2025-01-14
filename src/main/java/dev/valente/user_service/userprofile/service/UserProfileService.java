package dev.valente.user_service.userprofile.service;

import dev.valente.user_service.domain.User;
import dev.valente.user_service.domain.UserProfile;
import dev.valente.user_service.userprofile.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository profileRepository;

    public List<UserProfile> findAll() {
        return profileRepository.findAll();
    }

    public List<User> findAllByProfileId(Long id) {
        return profileRepository.findAllByProfile_Id(id).stream().map(UserProfile::getUser).collect(Collectors.toList());
    }

    public List<User> findAllUserByProfileId(Long id) {
        return profileRepository.findAllUsersByProfileId(id);
    }
}
