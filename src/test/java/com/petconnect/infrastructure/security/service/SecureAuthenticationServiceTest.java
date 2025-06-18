package com.petconnect.infrastructure.security.service;

import com.petconnect.infrastructure.security.audit.SecurityAuditService;
import com.petconnect.infrastructure.security.encryption.DataEncryptionService;
import com.petconnect.infrastructure.security.jwt.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecureAuthenticationServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private SecurityAuditService securityAuditService;

    @Mock
    private DataEncryptionService dataEncryptionService;

    @InjectMocks
    private SecureAuthenticationService secureAuthenticationService;

    @Mock
    private UserDetails userDetails;

    @Mock
    private Authentication authentication;

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "ValidPassword123!";
    private static final String TEST_IP = "192.168.1.1";
    private static final String TEST_TOKEN = "jwt-token";

    @BeforeEach
    void setUp() {
        reset(authenticationManager, userDetailsService, passwordEncoder, 
              jwtService, securityAuditService, dataEncryptionService);
    }

    @Test
    void shouldAuthenticateUserSuccessfully() {
        when(securityAuditService.isUserBlocked(TEST_EMAIL)).thenReturn(false);
        when(securityAuditService.canAttemptLogin(TEST_EMAIL)).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(TEST_EMAIL);
        when(jwtService.generateTokenWithUserInfo(any(), anyString(), anyString())).thenReturn(TEST_TOKEN);
        when(jwtService.generateRefreshToken(any())).thenReturn("refresh-token");
        when(dataEncryptionService.maskSensitiveData(anyString(), anyInt())).thenReturn("te*@***.com");

        SecureAuthenticationService.AuthenticationResult result = 
            secureAuthenticationService.authenticateUser(TEST_EMAIL, TEST_PASSWORD, TEST_IP);

        assertTrue(result.isSuccess());
        assertEquals(TEST_TOKEN, result.getAccessToken());
        assertEquals("refresh-token", result.getRefreshToken());
        verify(securityAuditService).recordLoginAttempt(TEST_EMAIL, true);
    }

    @Test
    void shouldFailWhenUserIsBlocked() {
        when(securityAuditService.isUserBlocked(TEST_EMAIL)).thenReturn(true);
        when(securityAuditService.getRemainingLockoutMinutes(TEST_EMAIL)).thenReturn(25L);

        SecureAuthenticationService.AuthenticationResult result = 
            secureAuthenticationService.authenticateUser(TEST_EMAIL, TEST_PASSWORD, TEST_IP);

        assertFalse(result.isSuccess());
        assertTrue(result.getErrorMessage().contains("bloqueada"));
        assertTrue(result.getErrorMessage().contains("25"));
        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    void shouldFailWhenCannotAttemptLogin() {
        when(securityAuditService.isUserBlocked(TEST_EMAIL)).thenReturn(false);
        when(securityAuditService.canAttemptLogin(TEST_EMAIL)).thenReturn(false);

        SecureAuthenticationService.AuthenticationResult result = 
            secureAuthenticationService.authenticateUser(TEST_EMAIL, TEST_PASSWORD, TEST_IP);

        assertFalse(result.isSuccess());
        assertTrue(result.getErrorMessage().contains("Muitas tentativas"));
        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    void shouldHandleBadCredentialsException() {
        when(securityAuditService.isUserBlocked(TEST_EMAIL)).thenReturn(false);
        when(securityAuditService.canAttemptLogin(TEST_EMAIL)).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        SecureAuthenticationService.AuthenticationResult result = 
            secureAuthenticationService.authenticateUser(TEST_EMAIL, TEST_PASSWORD, TEST_IP);

        assertFalse(result.isSuccess());
        assertTrue(result.getErrorMessage().contains("inválidas"));
        verify(securityAuditService).recordLoginAttempt(TEST_EMAIL, false);
    }

    @Test
    void shouldHandleDisabledException() {
        when(securityAuditService.isUserBlocked(TEST_EMAIL)).thenReturn(false);
        when(securityAuditService.canAttemptLogin(TEST_EMAIL)).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new DisabledException("Account disabled"));

        SecureAuthenticationService.AuthenticationResult result = 
            secureAuthenticationService.authenticateUser(TEST_EMAIL, TEST_PASSWORD, TEST_IP);

        assertFalse(result.isSuccess());
        assertTrue(result.getErrorMessage().contains("desabilitada"));
        verify(securityAuditService).recordLoginAttempt(TEST_EMAIL, false);
    }

    @Test
    void shouldValidateEmailFormat() {
        String invalidIdentifier = "ab";

        SecureAuthenticationService.AuthenticationResult result = 
            secureAuthenticationService.authenticateUser(invalidIdentifier, TEST_PASSWORD, TEST_IP);

        assertFalse(result.isSuccess());
        assertTrue(result.getErrorMessage().contains("Formato de identificador inválido"), 
            "Expected message to contain 'Formato de identificador inválido' but was: " + result.getErrorMessage());
    }

    @Test
    void shouldValidatePasswordLength() {
        String shortPassword = "123";

        SecureAuthenticationService.AuthenticationResult result = 
            secureAuthenticationService.authenticateUser(TEST_EMAIL, shortPassword, TEST_IP);

        assertFalse(result.isSuccess());
        assertTrue(result.getErrorMessage().contains("Senha muito curta"));
    }

    @Test
    void shouldCalculatePasswordStrength() {
        String weakPassword = "password";
        String strongPassword = "StrongP@ssw0rd123!";

        int weakScore = secureAuthenticationService.calculatePasswordStrength(weakPassword);
        int strongScore = secureAuthenticationService.calculatePasswordStrength(strongPassword);

        assertTrue(weakScore < strongScore);
        assertTrue(strongScore >= 80);
    }

    @Test
    void shouldEncryptPasswordSecurely() {
        String password = "TestPassword123!";
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encoded.hash");
        when(dataEncryptionService.generateSalt()).thenReturn("testsalt");

        SecureAuthenticationService.PasswordEncryptionResult result = 
            secureAuthenticationService.encryptPasswordWithDetails(password);

        assertNotNull(result);
        assertEquals("$2a$10$encoded.hash", result.getEncryptedPassword());
        assertEquals("testsalt", result.getSalt());
        assertTrue(result.getStrengthScore() > 0);
        verify(passwordEncoder).encode(password + "testsalt");
    }

    @Test
    void shouldRefreshTokenSuccessfully() {
        String refreshToken = "valid-refresh-token";
        when(jwtService.isTokenValid(refreshToken, null)).thenReturn(true);
        when(jwtService.extractUsername(refreshToken)).thenReturn(TEST_EMAIL);
        when(userDetailsService.loadUserByUsername(TEST_EMAIL)).thenReturn(userDetails);
        when(jwtService.generateTokenWithUserInfo(any(), anyString(), anyString())).thenReturn("new-access-token");
        when(jwtService.generateRefreshToken(any())).thenReturn("new-refresh-token");

        SecureAuthenticationService.AuthenticationResult result = 
            secureAuthenticationService.refreshToken(refreshToken);

        assertTrue(result.isSuccess());
        assertEquals("new-access-token", result.getAccessToken());
        assertEquals("new-refresh-token", result.getRefreshToken());
    }

    @Test
    void shouldFailRefreshTokenWithInvalidToken() {
        String invalidToken = "invalid-token";
        when(jwtService.isTokenValid(invalidToken, null)).thenReturn(false);

        SecureAuthenticationService.AuthenticationResult result = 
            secureAuthenticationService.refreshToken(invalidToken);

        assertFalse(result.isSuccess());
        assertTrue(result.getErrorMessage().contains("inválido"));
    }

    @Test
    void shouldValidateIdentifierFormats() {
        assertTrue(secureAuthenticationService.isValidEmail("test@example.com"));
        assertTrue(secureAuthenticationService.isValidEmail("user.name+tag@domain.co.uk"));
        assertFalse(secureAuthenticationService.isValidEmail("invalid-email"));
        assertFalse(secureAuthenticationService.isValidEmail("@domain.com"));
        assertFalse(secureAuthenticationService.isValidEmail("user@"));

        assertTrue(secureAuthenticationService.isValidUsername("username123"));
        assertTrue(secureAuthenticationService.isValidUsername("user_name"));
        assertTrue(secureAuthenticationService.isValidUsername("user.name"));
        assertFalse(secureAuthenticationService.isValidUsername("us"));
        assertFalse(secureAuthenticationService.isValidUsername("user name"));
        assertFalse(secureAuthenticationService.isValidUsername("user@name"));
    }

    @Test
    void shouldCalculatePasswordComplexity() {
        assertEquals(0, secureAuthenticationService.calculatePasswordStrength(""));
        assertEquals(0, secureAuthenticationService.calculatePasswordStrength(null));
        
        assertTrue(secureAuthenticationService.calculatePasswordStrength("password") < 50);
        assertTrue(secureAuthenticationService.calculatePasswordStrength("Password123") > 50);
        assertTrue(secureAuthenticationService.calculatePasswordStrength("P@ssw0rd123!") > 80);
    }

    @Test
    void shouldHandleNullAndEmptyInputs() {
        SecureAuthenticationService.AuthenticationResult result1 = 
            secureAuthenticationService.authenticateUser(null, TEST_PASSWORD, TEST_IP);
        SecureAuthenticationService.AuthenticationResult result2 = 
            secureAuthenticationService.authenticateUser(TEST_EMAIL, null, TEST_IP);
        SecureAuthenticationService.AuthenticationResult result3 = 
            secureAuthenticationService.authenticateUser("", TEST_PASSWORD, TEST_IP);

        assertFalse(result1.isSuccess());
        assertFalse(result2.isSuccess());
        assertFalse(result3.isSuccess());
    }
}
