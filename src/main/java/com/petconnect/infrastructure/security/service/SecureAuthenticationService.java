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
            
            if (!isValidEmail(identifier) && !isValidUsername(identifier)) {
                return AuthenticationResult.builder()
                    .success(false)
                    .errorMessage("Formato de identificador inválido")
                    .build();
            }
            
            if (password.length() > 256) {
                return AuthenticationResult.builder()
                    .success(false)
                    .errorMessage("Senha muito longa")
                    .build();
            }
            
            if (password.length() < 8) {
                return AuthenticationResult.builder()
                    .success(false)
                    .errorMessage("Senha muito curta")
                    .build();
            }

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

            if (!securityAuditService.canAttemptLogin(identifier)) {
                return AuthenticationResult.builder()
                    .success(false)
                    .errorMessage("Muitas tentativas de login. Aguarde antes de tentar novamente")
                    .build();
            }

            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(identifier, password)
            );
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            String accessToken = jwtService.generateTokenWithUserInfo(
                userDetails, userDetails.getUsername(), "USER");
            String refreshToken = jwtService.generateRefreshToken(userDetails);

            securityAuditService.recordLoginAttempt(identifier, true);
            
            log.info("Login bem-sucedido para usuário: {} de IP: {}", 
                dataEncryptionService.maskSensitiveData(identifier, 3), clientIp);
            
            return AuthenticationResult.builder()
                .success(true)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .username(userDetails.getUsername())
                .expiresIn(86400L)
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
            securityAuditService.recordLoginAttempt(identifier, false);
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
    
    public String encryptPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Senha não pode ser nula ou vazia");
        }

        String salt = dataEncryptionService.generateSalt();

        String hashedPassword = passwordEncoder.encode(plainPassword + salt);
        
        log.debug("Senha criptografada com sucesso");
        return hashedPassword;
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
    
    public PasswordEncryptionResult encryptPasswordWithDetails(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Senha não pode ser nula ou vazia");
        }
        
        String salt = dataEncryptionService.generateSalt();
        String hashedPassword = passwordEncoder.encode(password + salt);
        int strengthScore = calculatePasswordStrength(password);
        
        return PasswordEncryptionResult.builder()
            .encryptedPassword(hashedPassword)
            .salt(salt)
            .strengthScore(strengthScore)
            .build();
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
    
    @lombok.Data
    @lombok.Builder
    public static class PasswordEncryptionResult {
        private String encryptedPassword;
        private String salt;
        private int strengthScore;
    }
}
