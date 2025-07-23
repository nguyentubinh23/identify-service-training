package org.binhntt.identifyservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.binhntt.identifyservice.dto.request.ApiResponse;
import org.binhntt.identifyservice.dto.request.UserCreationRequest;
import org.binhntt.identifyservice.dto.request.UserUpdateRequest;
import org.binhntt.identifyservice.entity.User;
import org.binhntt.identifyservice.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping()
    ApiResponse<User> createUser(@RequestBody @Valid UserCreationRequest request) {
        ApiResponse<User> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.createUser(request));
        
        return apiResponse;
    }

    @GetMapping()
    List<User> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{userId}")
    User getUser(@PathVariable String userId) {
        return userService.getUser(userId);
    }

    @PutMapping("/{userId}")
    User updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request) {
        return userService.updateUser(userId, request);
    }

    @DeleteMapping("/{userId}")
    String deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return "User has been deleted";
    }
}
