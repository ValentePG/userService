package dev.valente.user_service.userprofile.controller;

import dev.valente.user_service.userprofile.dto.UserProfileGetResponse;
import dev.valente.user_service.userprofile.dto.UserProfileUserGetResponse;
import dev.valente.user_service.userprofile.mapper.UserProfileMapper;
import dev.valente.user_service.userprofile.service.UserProfileService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("v1/user-profiles")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "basicAuth")
public class UserProfileController {

    private final UserProfileService userProfileService;
    private final UserProfileMapper userProfileMapper;

    @GetMapping
    public ResponseEntity<List<UserProfileGetResponse>> findAll() {
        log.debug("Request received to list all user profiles.");

        var userProfiles = userProfileService.findAll();

        var userProfileGetResponses = userProfileMapper.toUserProfileGetResponse(userProfiles);

        return ResponseEntity.ok(userProfileGetResponses);
    }

    @GetMapping("profiles/{id}/users")
    public ResponseEntity<List<UserProfileUserGetResponse>> findById(@PathVariable Long id) {
        log.debug("Request received to find user profile by id: {}", id);

        var users = userProfileService.findAllUserByProfileId(id);

        var response = userProfileMapper.toUserProfileUserGetResponse(users);

        return ResponseEntity.ok(response);
    }
}
