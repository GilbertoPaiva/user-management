package com.petconnect.application.user.service;

import com.petconnect.application.user.usecase.AuthenticateUserUseCase;
import com.petconnect.domain.user.entity.User;
import com.petconnect.domain.user.port.UserRepositoryPort;
import com.petconnect.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

        if (password != null && !password.isEmpty() && !passwordEncoder.matches(password, user.getPassword())) {
            throw new BadRequestException("Email ou senha inválidos");
        }

        return user;
    }
}
