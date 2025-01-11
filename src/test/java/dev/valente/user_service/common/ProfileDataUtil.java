package dev.valente.user_service.common;

import dev.valente.user_service.domain.Profile;
import dev.valente.user_service.profile.mapper.ProfileMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProfileDataUtil {

    private final List<Profile> LIST_PROFILE = new ArrayList<>();

    {
        var profile1 = Profile.builder().id(1L).name("kogu").description("I am badass").build();
        var profile2 = Profile.builder().id(2L).name("jone").description("I am Intelligent").build();
        var profile3 = Profile.builder().id(3L).name("Fore").description("I am the force").build();

        var listProfile = List.of(profile1, profile2, profile3);
        LIST_PROFILE.addAll(listProfile);
    }

    public List<Profile> getListProfile() {
        return LIST_PROFILE;
    }

    public Profile getFirst(){
        return LIST_PROFILE.getFirst();
    }

    public Profile getSecond(){
        return LIST_PROFILE.get(1);
    }

    public Profile getLast(){
        return LIST_PROFILE.getLast();
    }

    public Profile getUserToSave(){
        return Profile.builder().name("sone").description("I am a new profile").build();
    }

    public Profile getUserAfterSave(){
        return Profile.builder().id(4L).name("sone").description("I am a new profile").build();
    }

    public Profile getUserToDelete(){
        return getLast();
    }
}
