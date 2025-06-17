package com.petconnect.domain.user.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

class UserTest {

    @Test
    void shouldCreateUserWithRequiredFields() {
        // Given
        UUID id = UUID.randomUUID();
        String email = "test@example.com";
        String username = "testuser";
        
        // When
        User user = User.builder()
                .id(id)
                .username(username)
                .email(email)
                .password("hashedPassword")
                .fullName("Test User")
                .userType(UserType.TUTOR)
                .active(true)
                .roles(Set.of("USER", "TUTOR"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        // Then
        assertNotNull(user);
        assertEquals(id, user.getId());
        assertEquals(email, user.getEmail());
        assertEquals(username, user.getUsername());
        assertTrue(user.isActive());
        assertEquals(UserType.TUTOR, user.getUserType());
    }

    @Test
    void shouldActivateUser() {
        // Given
        User user = User.builder()
                .active(false)
                .build();
        
        // When
        user.activate();
        
        // Then
        assertTrue(user.isActive());
    }

    @Test
    void shouldDeactivateUser() {
        // Given
        User user = User.builder()
                .active(true)
                .build();
        
        // When
        user.deactivate();
        
        // Then
        assertFalse(user.isActive());
    }

    @Test
    void shouldUpdatePassword() {
        // Given
        User user = User.builder()
                .password("oldPassword")
                .build();
        String newPassword = "newPassword";
        
        // When
        user.updatePassword(newPassword);
        
        // Then
        assertEquals(newPassword, user.getPassword());
    }

    @Test
    void shouldCheckUserRoles() {
        // Given
        User user = User.builder()
                .roles(Set.of("USER", "ADMIN"))
                .build();
        
        // When & Then
        assertTrue(user.hasRole("USER"));
        assertTrue(user.hasRole("ADMIN"));
        assertFalse(user.hasRole("MODERATOR"));
    }

    @Test
    void shouldCheckUserTypes() {
        // Given
        User adminUser = User.builder().userType(UserType.ADMIN).build();
        User tutorUser = User.builder().userType(UserType.TUTOR).build();
        User veterinarioUser = User.builder().userType(UserType.VETERINARIO).build();
        User lojistaUser = User.builder().userType(UserType.LOJISTA).build();
        
        // When & Then
        assertTrue(adminUser.isAdmin());
        assertFalse(adminUser.isTutor());
        
        assertTrue(tutorUser.isTutor());
        assertFalse(tutorUser.isAdmin());
        
        assertTrue(veterinarioUser.isVeterinario());
        assertFalse(veterinarioUser.isLojista());
        
        assertTrue(lojistaUser.isLojista());
        assertFalse(lojistaUser.isVeterinario());
    }
}
