package com.petconnect.application.user.usecase;

import com.petconnect.domain.user.entity.Role;
import com.petconnect.domain.user.entity.User;
import com.petconnect.domain.user.port.RoleRepositoryPort;
import com.petconnect.domain.user.port.UserRepositoryPort;
import com.petconnect.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CreateUserUseCase {

    private final UserRepositoryPort userRepository;
    private final RoleRepositoryPort roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User execute(CreateUserCommand command) {
        validateCommand(command);

        userRepository.findByEmail(command.getEmail()).ifPresent(user -> {
            throw new BadRequestException("Email já cadastrado.");
        });

        String roleName = "ROLE_" + command.getUserType().name();
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new BadRequestException("Role não encontrada: " + roleName));

        User user = User.builder()
                .email(command.getEmail())
                .password(command.getPassword())
                .fullName(command.getFullName())
                .userType(command.getUserType())
                .roles(Collections.singleton(role))
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return userRepository.save(user);
    }

    private void validateCommand(CreateUserCommand command) {
        if (command.getEmail() == null || command.getEmail().trim().isEmpty()) {
            throw new BadRequestException("Email é obrigatório.");
        }
        if (command.getPassword() == null || command.getPassword().isEmpty()) {
            throw new BadRequestException("Senha é obrigatória.");
        }
        if (command.getFullName() == null || command.getFullName().trim().isEmpty()) {
            throw new BadRequestException("Nome completo é obrigatório.");
        }
        if (command.getUserType() == null) {
            throw new BadRequestException("Tipo de usuário é obrigatório.");
        }
    }
}
