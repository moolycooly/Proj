package org.fintech.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.fintech.controllers.payload.JwtAuthenticationResponse;
import org.fintech.controllers.payload.ResetPasswordRequest;
import org.fintech.controllers.payload.SignInRequest;
import org.fintech.controllers.payload.SignUpRequest;
import org.fintech.services.auth.AuthenticationService;
import org.fintech.services.auth.LogoutService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth")
public class AuthController {
    private final AuthenticationService authenticationService;
    private final LogoutService logoutService;
    @Operation(summary = "User registration")
    @PostMapping("/sign-up")
    public JwtAuthenticationResponse signUp(@RequestBody @Valid SignUpRequest request) {
        return authenticationService.signUp(request);
    }

    @Operation(summary = "User auth")
    @PostMapping("/sign-in")
    public JwtAuthenticationResponse signIn(@RequestBody @Valid SignInRequest request) {
        return authenticationService.signIn(request);
    }
    @Operation(summary = "User logout")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            logoutService.invalidateToken(token);
        }
        else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You was not logged in");
        }

        return ResponseEntity.ok("You have successfully logged out");
    }
    @Operation(summary = "User reset password")
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody @Valid ResetPasswordRequest request, HttpServletRequest httpRequest) {
        String authHeader = httpRequest.getHeader("Authorization");
        JwtAuthenticationResponse response;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            response = authenticationService.resetPassword(request, token);
            logoutService.invalidateToken(token);
        }
        else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You was not logged in");
        }
        return ResponseEntity.ok(response);
    }
}