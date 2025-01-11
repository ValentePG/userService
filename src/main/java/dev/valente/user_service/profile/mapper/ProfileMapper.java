package dev.valente.user_service.profile.mapper;

import dev.valente.user_service.domain.Profile;
import dev.valente.user_service.profile.dto.get.ProfileGetResponse;
import dev.valente.user_service.profile.dto.post.ProfilePostRequest;
import dev.valente.user_service.profile.dto.post.ProfilePostResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProfileMapper {

    Profile profilePostRequestToProfile(ProfilePostRequest profilePostRequest);

    ProfilePostResponse profileToProfilePostResponse(Profile profile);

    ProfileGetResponse profileToProfileGetResponse(Profile profile);
}
