package dev.valente.user_service.userprofile.mapper;

import dev.valente.user_service.domain.User;
import dev.valente.user_service.domain.UserProfile;
import dev.valente.user_service.userprofile.dto.UserProfileGetResponse;
import dev.valente.user_service.userprofile.dto.UserProfileUserGetResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserProfileMapper {

    List<UserProfileGetResponse> toUserProfileGetResponse(List<UserProfile> userProfiles);

    List<UserProfileUserGetResponse> toUserProfileUserGetResponse(List<User> users);
}
