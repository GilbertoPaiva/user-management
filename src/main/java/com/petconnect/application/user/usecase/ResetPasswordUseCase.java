package com.petconnect.application.user.usecase;

import com.petconnect.domain.user.entity.User;
import com.petconnect.domain.user.port.UserRepositoryPort;
import com.petconnect.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ResetPasswordUseCase {
    
    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public String getSecurityQuestion(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com email: " + email));
        
        // Retorna a primeira pergunta de segurança do usuário
        if (user.getSecurityQuestions() != null) {
            return user.getSecurityQuestions().getQuestion1();
        }
        throw new ResourceNotFoundException("Pergunta de segurança não encontrada para o usuário");
    }
    
    @Transactional
    public void execute(String email, String securityAnswer, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com email: " + email));
        
        // Verifica se a resposta de segurança está correta
        if (user.getSecurityQuestions() == null || 
            !user.getSecurityQuestions().getAnswer1().equals(securityAnswer)) {
            throw new IllegalArgumentException("Resposta de segurança incorreta");
        }
        
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        userRepository.save(user);
    }
    
    @Transactional
    public void resetPassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com email: " + email));
        
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        userRepository.save(user);
    }
    
    @Transactional
    public void resetPasswordByUsername(String username, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com username: " + username));
        
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        userRepository.save(user);
    }
}
