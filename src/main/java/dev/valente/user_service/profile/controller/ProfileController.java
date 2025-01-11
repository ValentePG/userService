package dev.valente.user_service.profile.controller;

import dev.valente.user_service.profile.dto.get.ProfileGetResponse;
import dev.valente.user_service.profile.dto.post.ProfilePostRequest;
import dev.valente.user_service.profile.dto.post.ProfilePostResponse;
import dev.valente.user_service.profile.mapper.ProfileMapper;
import dev.valente.user_service.profile.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("v1/profiles")
public class ProfileController {

    private final ProfileMapper profileMapper;
    private final ProfileService profileService;

    @GetMapping
    public ResponseEntity<List<ProfileGetResponse>> findall(@RequestParam(required = false) String name){

        var response = profileService.findAll(name).stream().map(profileMapper::profileToProfileGetResponse).toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<ProfileGetResponse>> findAllPaginated(Pageable pageable){
        var response = profileService.findAllPaginated(pageable).map(profileMapper::profileToProfileGetResponse);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfileGetResponse> findById(@PathVariable Long id){

        var profileToFind = profileService.findByIdOrThrowNotFound(id);
        var response = profileMapper.profileToProfileGetResponse(profileToFind);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ProfilePostResponse> createProfile(@RequestBody @Valid ProfilePostRequest profilePostRequest){

        var profileToCreate = profileMapper.profilePostRequestToProfile(profilePostRequest);
        var profileCreated = profileService.createProfile(profileToCreate);
        var response = profileMapper.profileToProfilePostResponse(profileCreated);

        return ResponseEntity.status(201).body(response);
    }
}
