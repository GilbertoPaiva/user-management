package com.petconnect.infrastructure.adapter.web.controller;

import com.petconnect.application.user.usecase.AuthenticateUserUseCase;
import com.petconnect.application.user.usecase.CreateUserCommand;
import com.petconnect.application.user.usecase.ResetPasswordUseCase;
import com.petconnect.domain.user.entity.SecurityQuestions;
import com.petconnect.domain.user.entity.User;
import com.petconnect.domain.user.entity.UserProfile;
import com.petconnect.infrastructure.adapter.web.dto.CreateUserRequest;
import com.petconnect.infrastructure.adapter.web.dto.LoginRequest;
import com.petconnect.infrastructure.adapter.web.dto.UserResponse;
import com.petconnect.infrastructure.adapter.web.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthenticateUserUseCase authenticateUserUseCase;
    private final ResetPasswordUseCase resetPasswordUseCase;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody CreateUserRequest request) {
        SecurityQuestions securityQuestions = SecurityQuestions.builder()
                .question1(request.getSecurityQuestion1())
                .answer1(request.getSecurityAnswer1())
                .question2(request.getSecurityQuestion2())
                .answer2(request.getSecurityAnswer2())
                .question3(request.getSecurityQuestion3())
                .answer3(request.getSecurityAnswer3())
                .build();

        UserProfile userProfile = UserProfile.builder()
                .nome(request.getNome())
                .location(request.getLocation())
                .contactNumber(request.getContactNumber())
                .cnpj(request.getCnpj())
                .crmv(request.getCrmv())
                .storeType(request.getStoreType())
                .businessHours(request.getBusinessHours())
                .guardian(request.getGuardian())
                .build();

        CreateUserCommand command = CreateUserCommand.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .fullName(request.getFullName())
                .userType(request.getUserType())
                .roles(Set.of("USER", request.getUserType().name()))
                .securityQuestions(securityQuestions)
                .userProfile(userProfile)
                .build();

        User user = authenticateUserUseCase.createUser(command);
        UserResponse response = mapToUserResponse(user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Usuário criado com sucesso", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponse>> login(@Valid @RequestBody LoginRequest request) {
        User user = authenticateUserUseCase.execute(request.getEmail(), request.getPassword());
        UserResponse response = mapToUserResponse(user);

        return ResponseEntity.ok(ApiResponse.success("Login realizado com sucesso", response));
    }

    @GetMapping("/forgot-password/{email}")
    public ResponseEntity<ApiResponse<String>> getSecurityQuestion(@PathVariable String email) {
        String question = resetPasswordUseCase.getSecurityQuestion(email);
        return ResponseEntity.ok(ApiResponse.success("Pergunta de segurança", question));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @RequestParam String email,
            @RequestParam String securityAnswer,
            @RequestParam String newPassword) {
        
        resetPasswordUseCase.execute(email, securityAnswer, newPassword);
        return ResponseEntity.ok(ApiResponse.success("Senha redefinida com sucesso"));
    }

    private UserResponse mapToUserResponse(User user) {
        UserResponse.UserResponseBuilder builder = UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .userType(user.getUserType())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt());

        if (user.getUserProfile() != null) {
            UserProfile profile = user.getUserProfile();
            builder.nome(profile.getNome())
                   .location(profile.getLocation())
                   .contactNumber(profile.getContactNumber())
                   .cnpj(profile.getCnpj())
                   .crmv(profile.getCrmv())
                   .storeType(profile.getStoreType())
                   .businessHours(profile.getBusinessHours())
                   .guardian(profile.getGuardian());
        }

        return builder.build();
    }
}
