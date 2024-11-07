package org.fintech.services;

import lombok.RequiredArgsConstructor;
import org.fintech.dto.UserDto;
import org.fintech.exception.UserAlreadyExistsException;
import org.fintech.exception.UserNotFoundException;
import org.fintech.store.entity.UserEntity;
import org.fintech.store.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserEntity save(UserEntity userEntity) {
        return userRepository.save(userEntity);
    }

    public UserEntity create(UserEntity userEntity) {
        if (userRepository.existsByUsername(userEntity.getUsername())) {
            throw new UserAlreadyExistsException("User with this username already exists");
        }

        if (userRepository.existsByEmail(userEntity.getEmail())) {
            throw new UserAlreadyExistsException("User with this email already exists");
        }

        return save(userEntity);
    }

    public UserEntity getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public UserDetailsService userDetailsService() {
        return this::getByUsername;
    }

    public void update(UserDto user) {
        var userEntity = userRepository.findByUsername(user.getUsername()).orElseThrow(() -> new UserNotFoundException("User not found"));
        if(user.getEmail() != null) {
            if(userRepository.existsByEmail(user.getEmail())) {
                throw new UserAlreadyExistsException("User with this email already exists");
            }
            userEntity.setEmail(user.getEmail());
        }
        if(user.getPassword() != null) {
            userEntity.setPassword(user.getPassword());
        }
        userRepository.save(userEntity);
    }

    public UserEntity getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }
        var username = authentication.getName();
        return getByUsername(username);
    }
}