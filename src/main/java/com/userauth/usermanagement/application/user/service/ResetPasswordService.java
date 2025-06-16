package com.userauth.usermanagement.application.user.service;

import com.userauth.usermanagement.application.user.usecase.ResetPasswordUseCase;
import com.userauth.usermanagement.domain.user.entity.User;
import com.userauth.usermanagement.domain.user.port.UserRepositoryPort;
import com.userauth.usermanagement.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResetPasswordService implements ResetPasswordUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void execute(String email, String securityAnswer, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Email não encontrado"));

        if (user.getSecurityQuestions() == null) {
            throw new BadRequestException("Usuário não possui perguntas de segurança cadastradas");
        }

        String randomQuestion = user.getSecurityQuestions().getRandomQuestion();
        if (randomQuestion == null) {
            throw new BadRequestException("Nenhuma pergunta de segurança disponível");
        }

        if (!user.getSecurityQuestions().validateAnswer(randomQuestion, securityAnswer)) {
            throw new BadRequestException("Resposta da pergunta de segurança incorreta");
        }

        validatePassword(newPassword);
        user.updatePassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public String getSecurityQuestion(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Email não encontrado"));

        if (user.getSecurityQuestions() == null) {
            throw new BadRequestException("Usuário não possui perguntas de segurança cadastradas");
        }

        return user.getSecurityQuestions().getRandomQuestion();
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 6) {
            throw new BadRequestException("Senha deve ter pelo menos 6 caracteres");
        }
        if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$")) {
            throw new BadRequestException("Senha deve conter pelo menos uma letra maiúscula, uma minúscula, um número e um caractere especial");
        }
    }
}
