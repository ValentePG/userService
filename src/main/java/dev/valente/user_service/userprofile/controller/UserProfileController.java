package dev.valente.user_service.userprofile.controller;

import dev.valente.user_service.userprofile.dto.ProfilesGetResponse;
import dev.valente.user_service.userprofile.dto.UserProfileGetResponse;
import dev.valente.user_service.userprofile.mapper.UserProfileMapper;
import dev.valente.user_service.userprofile.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/user-profiles")
@RequiredArgsConstructor
@Slf4j
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

    @GetMapping("{id}")
    public ResponseEntity<List<ProfilesGetResponse>> findById(@PathVariable Long id) {

        var userProfiles = userProfileService.findAllByProfileId(id);

        var response = userProfileMapper.toProfilesGetResponse(userProfiles);

        return ResponseEntity.ok(response);
    }
}
