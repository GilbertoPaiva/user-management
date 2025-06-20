package com.petconnect.infrastructure.security.service;

import com.petconnect.infrastructure.security.audit.SecurityAuditService;
import com.petconnect.infrastructure.security.encryption.DataEncryptionService;
import com.petconnect.infrastructure.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecureAuthenticationService {
    
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final SecurityAuditService securityAuditService;
    private final DataEncryptionService dataEncryptionService;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9._-]{3,50}$");
    
    public AuthenticationResult authenticateUser(String identifier, String password, String clientIp) {
        try {
            if (identifier == null || identifier.trim().isEmpty()) {
                return AuthenticationResult.builder()
                    .success(false)
                    .errorMessage("Identificador é obrigatório")
                    .build();
            }
            if (password == null || password.trim().isEmpty()) {
                return AuthenticationResult.builder()
                    .success(false)
                    .errorMessage("Senha é obrigatória")
                    .build();
            }
            // Buscar usuário pelo identificador (email)
            UserDetails userDetails = (UserDetails) userDetailsService.loadUserByUsername(identifier);
            if (userDetails == null || !password.equals(userDetails.getPassword())) {
                securityAuditService.recordLoginAttempt(identifier, false);
                return AuthenticationResult.builder()
                    .success(false)
                    .errorMessage("Credenciais inválidas")
                    .build();
            }
            String accessToken = jwtService.generateTokenWithUserInfo(
                userDetails, userDetails.getUsername(), "USER");
            String refreshToken = jwtService.generateRefreshToken(userDetails);
            securityAuditService.recordLoginAttempt(identifier, true);
            return AuthenticationResult.builder()
                .success(true)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .username(userDetails.getUsername())
                .expiresIn(86400L)
                .build();
        } catch (Exception e) {
            securityAuditService.recordLoginAttempt(identifier, false);
            return AuthenticationResult.builder()
                .success(false)
                .errorMessage("Erro interno. Tente novamente mais tarde")
                .build();
        }
    }
    
    public PasswordValidationResult validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            return PasswordValidationResult.builder()
                .valid(false)
                .message("Senha é obrigatória")
                .strength(0)
                .build();
        }

        int strength = calculatePasswordStrength(password);
        
        Map<String, Boolean> criteria = new HashMap<>();
        criteria.put("length", password.length() >= 8);
        criteria.put("uppercase", password.matches(".*[A-Z].*"));
        criteria.put("lowercase", password.matches(".*[a-z].*"));
        criteria.put("digit", password.matches(".*[0-9].*"));
        criteria.put("special", password.matches(".*[@#$%^&+=!?*()_\\-\\[\\]{}|;:,.<>].*"));
        criteria.put("noWhitespace", !password.matches(".*\\s.*"));
        
        boolean isValid = criteria.values().stream().allMatch(Boolean::booleanValue) && strength >= 60;
        
        return PasswordValidationResult.builder()
            .valid(isValid)
            .strength(strength)
            .criteria(criteria)
            .message(isValid ? "Senha válida" : "Senha não atende aos critérios de segurança")
            .build();
    }
    
    public AuthenticationResult refreshToken(String refreshToken) {
        try {
            if (!jwtService.isTokenValid(refreshToken, null)) {
                return AuthenticationResult.builder()
                    .success(false)
                    .errorMessage("Token de refresh inválido")
                    .build();
            }
            
            String username = jwtService.extractUsername(refreshToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            
            String newAccessToken = jwtService.generateTokenWithUserInfo(
                userDetails, username, "USER");
            String newRefreshToken = jwtService.generateRefreshToken(userDetails);
            
            return AuthenticationResult.builder()
                .success(true)
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .username(username)
                .expiresIn(86400L)
                .build();
                
        } catch (Exception e) {
            log.error("Erro ao renovar token", e);
            return AuthenticationResult.builder()
                .success(false)
                .errorMessage("Erro ao renovar token")
                .build();
        }
    }
    
    public int calculatePasswordStrength(String password) {
        if (password == null || password.isEmpty()) {
            return 0;
        }
        
        int strength = 0;
        
        if (password.length() >= 8) strength += 20;
        if (password.length() >= 12) strength += 10;
        if (password.length() >= 16) strength += 10;
        
        if (password.matches(".*[a-z].*")) strength += 10;
        if (password.matches(".*[A-Z].*")) strength += 10;
        if (password.matches(".*[0-9].*")) strength += 10;
        if (password.matches(".*[@#$%^&+=!?*()_\\-\\[\\]{}|;:,.<>].*")) strength += 15;
        
        int uniqueChars = (int) password.chars().distinct().count();
        if (uniqueChars >= password.length() * 0.7) strength += 15;
        
        return Math.min(strength, 100);
    }
    
    public boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    public boolean isValidUsername(String username) {
        return username != null && USERNAME_PATTERN.matcher(username).matches();
    }
    
    @lombok.Data
    @lombok.Builder
    public static class AuthenticationResult {
        private boolean success;
        private String accessToken;
        private String refreshToken;
        private String username;
        private Long expiresIn;
        private String errorMessage;
    }
    
    @lombok.Data
    @lombok.Builder
    public static class PasswordValidationResult {
        private boolean valid;
        private int strength;
        private Map<String, Boolean> criteria;
        private String message;
    }
}
