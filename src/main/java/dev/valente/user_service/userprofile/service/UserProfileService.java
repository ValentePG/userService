package dev.valente.user_service.userprofile.service;

import dev.valente.user_service.domain.UserProfile;
import dev.valente.user_service.userprofile.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository profileRepository;

    public List<UserProfile> findAll() {
        return profileRepository.findAll();
    }

    public List<UserProfile> findAllByProfileId(Long id) {
        return profileRepository.findAllByProfile_Id(id);
    }
}
