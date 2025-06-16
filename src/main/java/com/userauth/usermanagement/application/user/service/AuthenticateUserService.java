package com.userauth.usermanagement.application.user.service;

import com.userauth.usermanagement.application.user.usecase.AuthenticateUserUseCase;
import com.userauth.usermanagement.application.user.usecase.CreateUserCommand;
import com.userauth.usermanagement.domain.user.entity.User;
import com.userauth.usermanagement.domain.user.port.UserRepositoryPort;
import com.userauth.usermanagement.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticateUserService implements AuthenticateUserUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User execute(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Email ou senha inválidos"));

        if (!user.isActive()) {
            throw new BadRequestException("Usuário inativo");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadRequestException("Email ou senha inválidos");
        }

        return user;
    }

    @Override
    public User createUser(CreateUserCommand command) {
        if (userRepository.existsByEmail(command.getEmail())) {
            throw new BadRequestException("Email já cadastrado");
        }

        User newUser = User.builder()
                .id(UUID.randomUUID())
                .username(command.getUsername())
                .email(command.getEmail())
                .password(passwordEncoder.encode(command.getPassword()))
                .fullName(command.getFullName())
                .userType(command.getUserType())
                .active(true)
                .roles(command.getRoles())
                .securityQuestions(command.getSecurityQuestions())
                .userProfile(command.getUserProfile())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return userRepository.save(newUser);
    }
}
