package org.binhntt.identifyservice.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.binhntt.identifyservice.dto.request.ApiResponse;
import org.binhntt.identifyservice.dto.request.AuthenticationRequest;
import org.binhntt.identifyservice.dto.response.AuthenticationResponse;
import org.binhntt.identifyservice.service.AuthenticationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/log-in")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        boolean result = authenticationService.authenticate(request);

        return ApiResponse.<AuthenticationResponse>builder().result(AuthenticationResponse.builder().authenticated(result).build()).build();
    }
}
