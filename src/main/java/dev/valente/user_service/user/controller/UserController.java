package dev.valente.user_service.user.controller;

import dev.valente.user_service.exception.ApiError;
import dev.valente.user_service.exception.DefaultMessage;
import dev.valente.user_service.user.dto.httprequest.get.UserGetResponse;
import dev.valente.user_service.user.dto.httprequest.post.UserPostRequest;
import dev.valente.user_service.user.dto.httprequest.post.UserPostResponse;
import dev.valente.user_service.user.dto.httprequest.put.UserPutRequest;
import dev.valente.user_service.user.service.UserMapperService;
import dev.valente.user_service.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;


import java.util.List;

@RestController
@RequestMapping("v1/users")
@RequiredArgsConstructor
@Tag(name = "User API", description = "User related endpoints")
public class UserController {
    private final UserService userService;
    private final UserMapperService userMapperService;

    @GetMapping
    @Operation(summary = "Get all users", description = "Get all users available in the system",
    responses = {
            @ApiResponse(description = "List all users",
            responseCode = "200",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(schema = @Schema(implementation = UserGetResponse.class))))
    })
    public ResponseEntity<List<UserGetResponse>> findAll() {

        var response = userService.findAll().stream().map(userMapperService::userToUserGetResponse).toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("{id}")
    @Operation(summary = "Get user by id",
            responses = {
                    @ApiResponse(description = "Get user by its id",
                            responseCode = "200",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserGetResponse.class))),
                    @ApiResponse(description = "User Not Found",
                            responseCode = "404",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = DefaultMessage.class)))
            })
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
    @Operation(summary = "Save user",
            requestBody =  @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Create User", required = true,
                content = @Content(schema = @Schema(implementation = UserPostRequest.class))),
            responses = {
                    @ApiResponse(description = "Create user in the system",
                            responseCode = "201",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserPostResponse.class))),
                    @ApiResponse(description = "Email already exists",
                            responseCode = "4.0.0",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = DefaultMessage.class))),
                    @ApiResponse(description = "Bad request invalid data",
                            responseCode = "400",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiError.class)))
            })
    public ResponseEntity<UserPostResponse> save(@RequestBody @Valid UserPostRequest userPostRequest) {

        var userMapped = userMapperService.userPostRequestToUser(userPostRequest);

        var userSaved = userService.save(userMapped);

        var UserPostResponse = userMapperService.userToUserPostResponse(userSaved);

        return ResponseEntity.status(201).body(UserPostResponse);

    }

    @PutMapping
    public ResponseEntity<Void> replace(@RequestBody @Valid UserPutRequest userPutRequest) {

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
