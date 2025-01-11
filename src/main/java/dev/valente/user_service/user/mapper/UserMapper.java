package dev.valente.user_service.user.mapper;

import dev.valente.user_service.domain.User;
import dev.valente.user_service.user.dto.httprequest.get.UserGetResponse;
import dev.valente.user_service.user.dto.httprequest.post.UserPostRequest;
import dev.valente.user_service.user.dto.httprequest.post.UserPostResponse;
import dev.valente.user_service.user.dto.httprequest.put.UserPutRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    UserGetResponse userToUserGetResponse(User user);

    User userPostRequestToUser(UserPostRequest userPostRequest);

    User userPutRequestToUser(UserPutRequest userPutRequest);

    UserPostResponse userToUserPostResponse(User user);

}
