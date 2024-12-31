package dev.valente.user_service.user.service;

import dev.valente.user_service.user.domain.User;
import dev.valente.user_service.user.dto.httprequest.get.UserGetResponse;
import dev.valente.user_service.user.dto.httprequest.post.UserPostRequest;
import dev.valente.user_service.user.dto.httprequest.post.UserPostResponse;
import dev.valente.user_service.user.dto.httprequest.put.UserPutRequest;
import dev.valente.user_service.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserMapperService {

    private final UserMapper userMapper;

    public UserGetResponse userToUserGetResponse(User user) {
        return userMapper.userToUserGetResponse(user);
    }

    public User userPostRequestToUser(UserPostRequest userPostRequest) {
        return userMapper.userPostRequestToUser(userPostRequest);
    }

    public User userPutRequestToUser(UserPutRequest userPostRequest) {
        return userMapper.userPutRequestToUser(userPostRequest);
    }

    public UserPostResponse userToUserPostResponse(User user) {
        return userMapper.userToUserPostResponse(user);
    }
}
