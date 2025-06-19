package com.petconnect.application.user.usecase;

import org.springframework.stereotype.Service;

@Service
public class ResetPasswordUseCase {
    public String getSecurityQuestion(String email) {
        // Implementação placeholder
        return "Pergunta de segurança padrão";
    }

    public void execute(String email, String securityAnswer, String newPassword) {
        // Implementação placeholder
    }
} 