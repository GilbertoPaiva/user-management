package com.userauth.usermanagement.domain.user.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private UUID id;
    private String username;
    private String email;
    private String password;
    private String fullName;
    private UserType userType;
    private boolean active;
    private Set<String> roles;
    private SecurityQuestions securityQuestions;
    private UserProfile userProfile;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }

    public boolean isAdmin() {
        return userType == UserType.ADMIN;
    }

    public boolean isLojista() {
        return userType == UserType.LOJISTA;
    }

    public boolean isTutor() {
        return userType == UserType.TUTOR;
    }

    public boolean isVeterinario() {
        return userType == UserType.VETERINARIO;
    }
}
