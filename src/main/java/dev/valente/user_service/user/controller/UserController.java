package dev.valente.user_service.user.controller;

import dev.valente.user_service.user.dto.httprequest.get.UserGetResponse;
import dev.valente.user_service.user.dto.httprequest.post.UserPostRequest;
import dev.valente.user_service.user.dto.httprequest.post.UserPostResponse;
import dev.valente.user_service.user.dto.httprequest.put.UserPutRequest;
import dev.valente.user_service.user.service.UserMapperService;
import dev.valente.user_service.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final UserMapperService userMapperService;

    @GetMapping
    public ResponseEntity<List<UserGetResponse>> findAll() {

        var response = userService.findAll().stream().map(userMapperService::userToUserGetResponse).toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("{id}")
    public ResponseEntity<UserGetResponse> findById(@PathVariable long id) {

        var userToMap = userService.findByIdOrThrowNotFound(id);

        var userGetResponse = userMapperService.userToUserGetResponse(userToMap);

        return ResponseEntity.ok(userGetResponse);
    }

    @GetMapping("find")
    public ResponseEntity<UserGetResponse> findWithParams(@RequestParam(required = false) String firstName
            , @RequestParam(required = false) String email) {

        if (firstName == null) {
            var userToMap = userService.findByEmailOrThrowNotFound(email);
            var userGetResponse = userMapperService.userToUserGetResponse(userToMap);
            return ResponseEntity.ok(userGetResponse);
        }

        var userToMap = userService.findByFirstNameOrThrowNotFound(firstName);

        var userGetResponse = userMapperService.userToUserGetResponse(userToMap);

        return ResponseEntity.ok(userGetResponse);
    }

    @PostMapping
    public ResponseEntity<UserPostResponse> save(@RequestBody @Valid UserPostRequest userPostRequest) {

        var userMapped = userMapperService.userPostRequestToUser(userPostRequest);

        var userSaved = userService.save(userMapped);

        var UserPostResponse = userMapperService.userToUserPostResponse(userSaved);

        return ResponseEntity.status(201).body(UserPostResponse);

    }

    @PutMapping
    public ResponseEntity<Void> replace(@RequestBody UserPutRequest userPutRequest) {

        var userToReplace = userMapperService.userPutRequestToUser(userPutRequest);

        userService.replace(userToReplace);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteById(@PathVariable long id) {

        userService.delete(id);

        return ResponseEntity.noContent().build();
    }

}
