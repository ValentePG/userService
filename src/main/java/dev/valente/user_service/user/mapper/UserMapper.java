package dev.valente.user_service.user.mapper;

import dev.valente.user_service.annotation.EncoderMapping;
import dev.valente.user_service.domain.User;
import dev.valente.user_service.user.dto.httprequest.get.UserGetResponse;
import dev.valente.user_service.user.dto.httprequest.post.UserPostRequest;
import dev.valente.user_service.user.dto.httprequest.post.UserPostResponse;
import dev.valente.user_service.user.dto.httprequest.put.UserPutRequest;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
uses = PasswordEncoderMapper.class)
public interface UserMapper {

    UserGetResponse userToUserGetResponse(User user);

    @Mapping(target = "roles", constant = "USER")
    @Mapping(target = "password", qualifiedBy = EncoderMapping.class)
    User userPostRequestToUser(UserPostRequest userPostRequest);

    @Mapping(target = "password", qualifiedBy = EncoderMapping.class)
    User userPutRequestToUser(UserPutRequest userPutRequest);

    UserPostResponse userToUserPostResponse(User user);

//    @Mapping(target = "password", source = "rawPassword", qualifiedBy = EncoderMapping.class)
//    @Mapping(target = "roles", source = "savedUser.roles")
//    @Mapping(target = "id", source = "userToUpdate.id")
//    @Mapping(target = "firstName", source = "userToUpdate.firstName")
//    @Mapping(target = "lastName", source = "userToUpdate.lastName")
//    @Mapping(target = "email", source = "userToUpdate.email")
//    User toUserWithPasswordAndRoles(User userToUpdate, String rawPassword, User savedUser);

//    @AfterMapping
//    default void setPasswordIfNull(@MappingTarget User user, String rawPassword, User savedUser) {
//        if (rawPassword == null) {
//            user.setPassword(savedUser.getPassword());
//        }
//    }
}
