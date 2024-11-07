package org.fintech.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.fintech.controllers.payload.JwtAuthenticationResponse;
import org.fintech.controllers.payload.ResetPasswordRequest;
import org.fintech.controllers.payload.SignInRequest;
import org.fintech.controllers.payload.SignUpRequest;
import org.fintech.services.auth.AuthenticationService;
import org.fintech.services.auth.LogoutService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private LogoutService logoutService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void signUp_ValidRequest_ShouldReturnJwtAuthenticationResponse() {
        // given
        SignUpRequest request = new SignUpRequest("user", "email@example.com", "password");
        JwtAuthenticationResponse expectedResponse = new JwtAuthenticationResponse("token");
        when(authenticationService.signUp(any(SignUpRequest.class))).thenReturn(expectedResponse);

        // when
        JwtAuthenticationResponse actualResponse = authController.signUp(request);

        // then
        assertNotNull(actualResponse);
        assertEquals(expectedResponse, actualResponse);
        verify(authenticationService, times(1)).signUp(request);
    }

    @Test
    void signUp_InvalidRequest_ShouldThrowException() {
        // given
        SignUpRequest invalidRequest = new SignUpRequest("", "email", ""); // Некорректные данные
        //when
        when(authenticationService.signUp(any(SignUpRequest.class)))
                .thenThrow(new IllegalArgumentException("Invalid data"));

        //then
        assertThrows(IllegalArgumentException.class, () -> authController.signUp(invalidRequest));
        verify(authenticationService, times(1)).signUp(invalidRequest);
    }

    @Test
    void signIn_ValidRequest_ShouldReturnJwtAuthenticationResponse() {
        // given
        SignInRequest request = new SignInRequest("user", "password", true);
        JwtAuthenticationResponse expectedResponse = new JwtAuthenticationResponse("token");
        when(authenticationService.signIn(any(SignInRequest.class))).thenReturn(expectedResponse);

        // when
        JwtAuthenticationResponse actualResponse = authController.signIn(request);

        // then
        assertNotNull(actualResponse);
        assertEquals(expectedResponse, actualResponse);
        verify(authenticationService, times(1)).signIn(request);
    }

    @Test
    void signIn_InvalidCredentials_ShouldThrowException() {
        // when
        SignInRequest invalidRequest = new SignInRequest("user", "wrongPassword", false);
        when(authenticationService.signIn(any(SignInRequest.class)))
                .thenThrow(new IllegalArgumentException("Invalid credentials"));

        // then
        assertThrows(IllegalArgumentException.class, () -> authController.signIn(invalidRequest));
        verify(authenticationService, times(1)).signIn(invalidRequest);
    }

    @Test
    void logout_WithBearerToken_ShouldInvalidateTokenAndReturnSuccessMessage() {
        // when
        String token = "token";
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer " + token);

        // given
        ResponseEntity<?> response = authController.logout(httpServletRequest);

        // then
        assertEquals(ResponseEntity.ok("You have successfully logged out"), response);
        verify(logoutService, times(1)).invalidateToken(token);
    }

    @Test
    void resetPassword_WithValidToken_ShouldResetPasswordAndReturnJwtAuthenticationResponse() {
        // given
        ResetPasswordRequest request = new ResetPasswordRequest("newPassword", "0000");
        JwtAuthenticationResponse expectedResponse = new JwtAuthenticationResponse("token");
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer token");
        when(authenticationService.resetPassword(request, "token")).thenReturn(expectedResponse);

        // when
        ResponseEntity<JwtAuthenticationResponse> response = (ResponseEntity<JwtAuthenticationResponse>) authController.resetPassword(request, httpServletRequest);

        // then
        assertEquals(ResponseEntity.ok(expectedResponse), response);
        verify(authenticationService, times(1)).resetPassword(request, "token");
        verify(logoutService, times(1)).invalidateToken("token");
    }

    @Test
    void resetPassword_InvalidConfirmationCode_ShouldThrowException() {
        // given
        ResetPasswordRequest request = new ResetPasswordRequest("newPassword", "12345");
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer token");
        when(authenticationService.resetPassword(request, "token")).thenThrow(new IllegalArgumentException("Invalid confirmation code"));

        // then
        assertThrows(IllegalArgumentException.class, () -> authController.resetPassword(request, httpServletRequest));
        verify(authenticationService, times(1)).resetPassword(request, "token");
    }

    @Test
    void resetPassword_WithInvalidToken_ShouldReturnUnauthorized() {
        // given
        ResetPasswordRequest request = new ResetPasswordRequest("newPassword", "12345");
        when(httpServletRequest.getHeader("Authorization")).thenReturn(null);

        // when
        ResponseEntity<JwtAuthenticationResponse> response = (ResponseEntity<JwtAuthenticationResponse>) authController.resetPassword(request, httpServletRequest);

        // then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(authenticationService, never()).resetPassword(request, "token");
        verify(logoutService, never()).invalidateToken(anyString());
    }
}