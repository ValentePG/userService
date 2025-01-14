package dev.valente.user_service.userprofile.mapper;

import dev.valente.user_service.domain.UserProfile;
import dev.valente.user_service.userprofile.dto.ProfilesGetResponse;
import dev.valente.user_service.userprofile.dto.UserProfileGetResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserProfileMapper {

    List<UserProfileGetResponse> toUserProfileGetResponse(List<UserProfile> userProfiles);

    List<ProfilesGetResponse> toProfilesGetResponse(List<UserProfile> userProfiles);
}
