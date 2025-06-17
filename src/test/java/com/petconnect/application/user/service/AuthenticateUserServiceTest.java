package com.petconnect.application.user.service;

import com.petconnect.application.user.usecase.CreateUserCommand;
import com.petconnect.domain.user.entity.SecurityQuestions;
import com.petconnect.domain.user.entity.User;
import com.petconnect.domain.user.entity.UserProfile;
import com.petconnect.domain.user.entity.UserType;
import com.petconnect.domain.user.port.UserRepositoryPort;
import com.petconnect.exception.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticateUserServiceTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthenticateUserService authenticateUserService;

    private User testUser;
    private CreateUserCommand testCommand;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .password("$2a$10$hashedPassword")
                .fullName("Test User")
                .userType(UserType.TUTOR)
                .active(true)
                .roles(Set.of("USER", "TUTOR"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        SecurityQuestions securityQuestions = SecurityQuestions.builder()
                .question1("What is your pet's name?")
                .answer1("Fluffy")
                .question2("What is your favorite color?")
                .answer2("Blue")
                .question3("What city were you born in?")
                .answer3("New York")
                .build();

        UserProfile userProfile = UserProfile.builder()
                .nome("Test User Profile")
                .location("Test City")
                .contactNumber("123456789")
                .build();

        testCommand = CreateUserCommand.builder()
                .username("testuser")
                .email("newuser@example.com")
                .password("TestPassword123!")
                .fullName("New Test User")
                .userType(UserType.TUTOR)
                .roles(Set.of("USER", "TUTOR"))
                .securityQuestions(securityQuestions)
                .userProfile(userProfile)
                .build();

        reset(userRepository, passwordEncoder);
    }

    @Test
    void shouldAuthenticateUserWithValidCredentials() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", testUser.getPassword())).thenReturn(true);

        User result = authenticateUserService.execute("test@example.com", "password123");

        assertNotNull(result);
        assertEquals(testUser.getEmail(), result.getEmail());
        assertEquals(testUser.getId(), result.getId());
        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder).matches("password123", testUser.getPassword());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            authenticateUserService.execute("nonexistent@example.com", "password123");
        });

        assertEquals("Email ou senha inválidos", exception.getMessage());
        verify(userRepository).findByEmail("nonexistent@example.com");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void shouldThrowExceptionWhenUserIsInactive() {
        testUser.deactivate();
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            authenticateUserService.execute("test@example.com", "password123");
        });

        assertEquals("Usuário inativo", exception.getMessage());
        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsInvalid() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongpassword", testUser.getPassword())).thenReturn(false);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            authenticateUserService.execute("test@example.com", "wrongpassword");
        });

        assertEquals("Email ou senha inválidos", exception.getMessage());
        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder).matches("wrongpassword", testUser.getPassword());
    }

    @Test
    void shouldAuthenticateWithEmptyPasswordForRefreshToken() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        User result = authenticateUserService.execute("test@example.com", "");

        assertNotNull(result);
        assertEquals(testUser.getEmail(), result.getEmail());
        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void shouldAuthenticateWithNullPasswordForRefreshToken() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        User result = authenticateUserService.execute("test@example.com", null);

        assertNotNull(result);
        assertEquals(testUser.getEmail(), result.getEmail());
        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void shouldCreateUserSuccessfully() {
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode("TestPassword123!")).thenReturn("$2a$10$hashedNewPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = authenticateUserService.createUser(testCommand);

        assertNotNull(result);
        assertEquals("newuser@example.com", result.getEmail());
        assertEquals("testuser", result.getUsername());
        assertEquals("New Test User", result.getFullName());
        assertEquals(UserType.TUTOR, result.getUserType());
        assertTrue(result.isActive());
        assertEquals(Set.of("USER", "TUTOR"), result.getRoles());
        assertNotNull(result.getSecurityQuestions());
        assertNotNull(result.getUserProfile());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());

        verify(userRepository).existsByEmail("newuser@example.com");
        verify(passwordEncoder).encode("TestPassword123!");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(true);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            authenticateUserService.createUser(testCommand);
        });

        assertEquals("Email já cadastrado", exception.getMessage());
        verify(userRepository).existsByEmail("newuser@example.com");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldCreateUserWithAllUserTypes() {
        UserType[] userTypes = {UserType.TUTOR, UserType.VETERINARIO, UserType.LOJISTA, UserType.ADMIN};

        for (UserType userType : userTypes) {
            CreateUserCommand command = CreateUserCommand.builder()
                    .username("user" + userType.name().toLowerCase())
                    .email("user" + userType.name().toLowerCase() + "@example.com")
                    .password("TestPassword123!")
                    .fullName("Test " + userType.name())
                    .userType(userType)
                    .roles(Set.of("USER", userType.name()))
                    .build();

            when(userRepository.existsByEmail(command.getEmail())).thenReturn(false);
            when(passwordEncoder.encode(command.getPassword())).thenReturn("$2a$10$hashedPassword");
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            User result = authenticateUserService.createUser(command);

            assertEquals(userType, result.getUserType());
            assertTrue(result.getRoles().contains(userType.name()));

            reset(userRepository, passwordEncoder);
        }
    }

    @Test
    void shouldCreateUserWithMinimalInformation() {
        CreateUserCommand minimalCommand = CreateUserCommand.builder()
                .username("minimaluser")
                .email("minimal@example.com")
                .password("TestPassword123!")
                .fullName("Minimal User")
                .userType(UserType.TUTOR)
                .roles(Set.of("USER"))
                .build();

        when(userRepository.existsByEmail("minimal@example.com")).thenReturn(false);
        when(passwordEncoder.encode("TestPassword123!")).thenReturn("$2a$10$hashedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = authenticateUserService.createUser(minimalCommand);

        assertNotNull(result);
        assertEquals("minimal@example.com", result.getEmail());
        assertNull(result.getSecurityQuestions());
        assertNull(result.getUserProfile());
    }

    @Test
    void shouldSetTimestampsWhenCreatingUser() {
        LocalDateTime beforeCreation = LocalDateTime.now().minusSeconds(1);
        
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode("TestPassword123!")).thenReturn("$2a$10$hashedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = authenticateUserService.createUser(testCommand);

        LocalDateTime afterCreation = LocalDateTime.now().plusSeconds(1);

        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
        assertTrue(result.getCreatedAt().isAfter(beforeCreation));
        assertTrue(result.getCreatedAt().isBefore(afterCreation));
        assertEquals(result.getCreatedAt(), result.getUpdatedAt());
    }

    @Test
    void shouldGenerateUniqueIdWhenCreatingUser() {
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode("TestPassword123!")).thenReturn("$2a$10$hashedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = authenticateUserService.createUser(testCommand);

        assertNotNull(result.getId());
        assertTrue(result.getId() instanceof UUID);
    }

    @Test
    void shouldHandleRepositoryExceptionDuringUserCreation() {
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode("TestPassword123!")).thenReturn("$2a$10$hashedPassword");
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> {
            authenticateUserService.createUser(testCommand);
        });

        verify(userRepository).save(any(User.class));
    }
}
