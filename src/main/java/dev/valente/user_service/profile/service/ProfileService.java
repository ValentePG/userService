package dev.valente.user_service.profile.service;

import dev.valente.user_service.domain.Profile;
import dev.valente.user_service.exception.NotFoundException;
import dev.valente.user_service.exception.UserNameAlreadyExists;
import dev.valente.user_service.profile.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;

    public Profile createProfile(Profile profile) {
        assertNameDoesNotExist(profile.getName());
        return profileRepository.save(profile);
    }

    public List<Profile> findAll(String name) {
        if(name != null){
            return profileRepository.findAllByName(name);
        }
        return profileRepository.findAll();
    }

    public Page<Profile> findAllPaginated(Pageable pageable) {
        return profileRepository.findAll(pageable);
    }

    public Profile findByIdOrThrowNotFound(Long id) {
        return profileRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Profile not found!"));
    }

//    public Profile findByNameOrThrowNotFound(String name) {
//        return profileRepository.findByName(name)
//                .orElseThrow(() -> new NotFoundException("Profile not found!"));
//    }

    private void assertNameDoesNotExist(String name) {
         profileRepository.findByName(name).ifPresent(this::throwsUserNameAlreadyExists);
    }

    private void throwsUserNameAlreadyExists(Profile profile) {
        throw new UserNameAlreadyExists("Profile with name %s already exists!".formatted(profile.getName()));
    }

}
