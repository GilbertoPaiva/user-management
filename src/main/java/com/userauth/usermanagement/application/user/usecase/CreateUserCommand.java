package com.userauth.usermanagement.application.user.usecase;

import com.userauth.usermanagement.domain.user.entity.SecurityQuestions;
import com.userauth.usermanagement.domain.user.entity.UserProfile;
import com.userauth.usermanagement.domain.user.entity.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserCommand {
    private String username;
    private String email;
    private String password;
    private String fullName;
    private UserType userType;
    private Set<String> roles;
    private SecurityQuestions securityQuestions;
    private UserProfile userProfile;
}
