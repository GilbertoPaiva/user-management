package com.petconnect.application.user.usecase;

import com.petconnect.domain.user.entity.SecurityQuestions;
import com.petconnect.domain.user.entity.UserProfile;
import com.petconnect.domain.user.entity.UserType;
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
}
