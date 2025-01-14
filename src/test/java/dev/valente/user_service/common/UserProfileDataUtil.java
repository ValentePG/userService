package dev.valente.user_service.common;

import dev.valente.user_service.domain.UserProfile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserProfileDataUtil {

    private final ProfileDataUtil profileDataUtil;
    private final UserDataUtil userDataUtil;
    private final List<UserProfile> LIST_USER_PROFILE = new ArrayList<>();

    public UserProfileDataUtil(ProfileDataUtil profileDataUtil, UserDataUtil userDataUtil) {
        this.profileDataUtil = profileDataUtil;
        this.userDataUtil = userDataUtil;
        newProfileList();
    }

    public void newProfileList() {
        var userProfile1 = UserProfile.builder().id(1L).user(userDataUtil.getListUsers().get(0))
                .profile(profileDataUtil.getFirst()).build();

        var userProfile2 = UserProfile.builder().id(2L).user(userDataUtil.getListUsers().get(1))
                .profile(profileDataUtil.getFirst()).build();

        var userProfile3 = UserProfile.builder().id(3L).user(userDataUtil.getListUsers().get(2))
                .profile(profileDataUtil.getSecond()).build();


        var listUserProfile = List.of(userProfile1, userProfile2, userProfile3);
        LIST_USER_PROFILE.addAll(listUserProfile);
    }

    public List<UserProfile> getListUserProfile() {
        return LIST_USER_PROFILE;
    }

    public UserProfile getFirst() {
        return LIST_USER_PROFILE.getFirst();
    }

    public UserProfile getSecond() {
        return LIST_USER_PROFILE.get(1);
    }

    public UserProfile getLast() {
        return LIST_USER_PROFILE.getLast();
    }

}
