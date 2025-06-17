package com.petconnect.infrastructure.security.service;

import com.petconnect.infrastructure.security.audit.SecurityAuditService;
import com.petconnect.infrastructure.security.encryption.DataEncryptionService;
import com.petconnect.infrastructure.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecureAuthenticationService {
    
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final SecurityAuditService securityAuditService;
    private final DataEncryptionService dataEncryptionService;
    
    // Padrões para validação de entrada
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9._-]{3,50}$");
    
    /**
     * Autenticação segura com múltiplas validações
     */
    public AuthenticationResult authenticateUser(String identifier, String password, String clientIp) {
        try {
            // Validar entrada
            validateAuthenticationInput(identifier, password);
            
            // Verificar se usuário está bloqueado
            if (securityAuditService.isUserBlocked(identifier)) {
                long remainingMinutes = securityAuditService.getRemainingLockoutMinutes(identifier);
                securityAuditService.recordUnauthorizedAccess("LOGIN", 
                    "Tentativa de login com conta bloqueada: " + identifier);
                
                return AuthenticationResult.builder()
                    .success(false)
                    .errorMessage("Conta temporariamente bloqueada. Tente novamente em " + 
                                remainingMinutes + " minuto(s)")
                    .build();
            }
            
            // Verificar se pode tentar login
            if (!securityAuditService.canAttemptLogin(identifier)) {
                return AuthenticationResult.builder()
                    .success(false)
                    .errorMessage("Muitas tentativas de login. Aguarde antes de tentar novamente")
                    .build();
            }
            
            // Tentar autenticação
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(identifier, password)
            );
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            
            // Gerar tokens JWT
            String accessToken = jwtService.generateTokenWithUserInfo(
                userDetails, userDetails.getUsername(), "USER");
            String refreshToken = jwtService.generateRefreshToken(userDetails);
            
            // Registrar sucesso
            securityAuditService.recordLoginAttempt(identifier, true);
            
            log.info("Login bem-sucedido para usuário: {} de IP: {}", 
                dataEncryptionService.maskSensitiveData(identifier, 3), clientIp);
            
            return AuthenticationResult.builder()
                .success(true)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .username(userDetails.getUsername())
                .expiresIn(86400L) // 24 horas
                .build();
                
        } catch (BadCredentialsException e) {
            securityAuditService.recordLoginAttempt(identifier, false);
            log.warn("Tentativa de login com credenciais inválidas para: {} de IP: {}", 
                dataEncryptionService.maskSensitiveData(identifier, 3), clientIp);
            
            return AuthenticationResult.builder()
                .success(false)
                .errorMessage("Credenciais inválidas")
                .build();
                
        } catch (DisabledException e) {
            securityAuditService.recordSecurityViolation("DISABLED_ACCOUNT_ACCESS", 
                "Tentativa de acesso com conta desabilitada: " + identifier);
            
            return AuthenticationResult.builder()
                .success(false)
                .errorMessage("Conta desabilitada")
                .build();
                
        } catch (Exception e) {
            log.error("Erro durante autenticação para usuário: {}", 
                dataEncryptionService.maskSensitiveData(identifier, 3), e);
            
            securityAuditService.recordSecurityViolation("AUTHENTICATION_ERROR", 
                "Erro interno durante autenticação: " + e.getMessage());
            
            return AuthenticationResult.builder()
                .success(false)
                .errorMessage("Erro interno. Tente novamente mais tarde")
                .build();
        }
    }
    
    /**
     * Validação de senha com critérios rigorosos
     */
    public PasswordValidationResult validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            return PasswordValidationResult.builder()
                .valid(false)
                .message("Senha é obrigatória")
                .strength(0)
                .build();
        }
        
        // Calcular força da senha
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
    
    /**
     * Criptografia segura de senha
     */
    public String encryptPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Senha não pode ser nula ou vazia");
        }
        
        // Gerar salt único
        String salt = dataEncryptionService.generateSalt();
        
        // Criptografar senha com BCrypt (já inclui salt)
        String hashedPassword = passwordEncoder.encode(plainPassword + salt);
        
        log.debug("Senha criptografada com sucesso");
        return hashedPassword;
    }
    
    /**
     * Refresh token seguro
     */
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
    
    private void validateAuthenticationInput(String identifier, String password) {
        if (identifier == null || identifier.trim().isEmpty()) {
            throw new IllegalArgumentException("Identificador é obrigatório");
        }
        
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Senha é obrigatória");
        }
        
        // Validar formato do identificador (email ou username)
        if (!EMAIL_PATTERN.matcher(identifier).matches() && 
            !USERNAME_PATTERN.matcher(identifier).matches()) {
            throw new IllegalArgumentException("Formato de identificador inválido");
        }
        
        // Verificar comprimento da senha
        if (password.length() > 256) {
            throw new IllegalArgumentException("Senha muito longa");
        }
    }
    
    private int calculatePasswordStrength(String password) {
        int strength = 0;
        
        // Comprimento
        if (password.length() >= 8) strength += 20;
        if (password.length() >= 12) strength += 10;
        if (password.length() >= 16) strength += 10;
        
        // Variedade de caracteres
        if (password.matches(".*[a-z].*")) strength += 10;
        if (password.matches(".*[A-Z].*")) strength += 10;
        if (password.matches(".*[0-9].*")) strength += 10;
        if (password.matches(".*[@#$%^&+=!?*()_\\-\\[\\]{}|;:,.<>].*")) strength += 15;
        
        // Complexidade
        int uniqueChars = (int) password.chars().distinct().count();
        if (uniqueChars >= password.length() * 0.7) strength += 15;
        
        return Math.min(strength, 100);
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
