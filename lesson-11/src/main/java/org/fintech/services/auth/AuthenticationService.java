package org.fintech.services.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fintech.controllers.payload.JwtAuthenticationResponse;
import org.fintech.controllers.payload.ResetPasswordRequest;
import org.fintech.controllers.payload.SignInRequest;
import org.fintech.controllers.payload.SignUpRequest;
import org.fintech.dto.UserDto;
import org.fintech.services.UserService;
import org.fintech.store.entity.UserEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@EnableScheduling
@Slf4j
public class AuthenticationService {
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    public JwtAuthenticationResponse signUp(SignUpRequest request) {
        var user = UserEntity.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("USER")
                .build();

        userService.create(user);
        var jwt = jwtService.generateToken(user, false);
        return new JwtAuthenticationResponse(jwt);
    }

    public JwtAuthenticationResponse signIn(SignInRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
        ));

        var user = userService.userDetailsService().loadUserByUsername(request.getUsername());

        return new JwtAuthenticationResponse(jwtService.generateToken(user, request.getRememberMe()));
    }
    public JwtAuthenticationResponse resetPassword(ResetPasswordRequest request, String token) {
        if (!"0000".equals(request.getConfirmationCode())) {
            throw new IllegalArgumentException("Неверный код подтверждения");
        }
        String username = jwtService.extractUserName(token);
        userService.update(UserDto.builder()
                        .username(username)
                        .password(request.getPassword())
                        .build());
        var user = userService.userDetailsService().loadUserByUsername(username);
        return new JwtAuthenticationResponse(jwtService.generateToken(user, false));
    }



}
