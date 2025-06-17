package com.petconnect.infrastructure.adapter.web.controller;

import com.petconnect.application.user.service.AuthenticateUserService;
import com.petconnect.application.user.usecase.CreateUserCommand;
import com.petconnect.application.user.usecase.ResetPasswordUseCase;
import com.petconnect.domain.user.entity.SecurityQuestions;
import com.petconnect.domain.user.entity.User;
import com.petconnect.domain.user.entity.UserProfile;
import com.petconnect.infrastructure.adapter.web.dto.*;
import com.petconnect.infrastructure.adapter.web.shared.dto.ApiResponse;
import com.petconnect.infrastructure.security.interceptor.SecurityInterceptor;
import com.petconnect.infrastructure.security.jwt.JwtService;
import com.petconnect.infrastructure.security.service.CustomUserDetailsService;
import com.petconnect.infrastructure.security.service.SecureAuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {
    
    private final AuthenticateUserService authenticateUserService;
    private final ResetPasswordUseCase resetPasswordUseCase;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final SecureAuthenticationService secureAuthenticationService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody CreateUserRequest request) {
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

        User user = authenticateUserService.createUser(command);
        
        // Gerar tokens JWT
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String accessToken = jwtService.generateTokenWithUserInfo(
                userDetails, 
                user.getId().toString(), 
                user.getUserType().name()
        );
        String refreshToken = jwtService.generateRefreshToken(userDetails);
        
        AuthResponse response = AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(mapToUserInfo(user))
                .build();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Usuário criado com sucesso", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request, 
                                                          HttpServletRequest httpRequest) {
        // Obter IP do cliente via SecurityInterceptor
        String clientIp = SecurityInterceptor.getCurrentClientIp();
        if (clientIp == null) {
            clientIp = httpRequest.getRemoteAddr();
        }
        
        // Usar o serviço de autenticação segura
        SecureAuthenticationService.AuthenticationResult result = 
            secureAuthenticationService.authenticateUser(request.getEmail(), request.getPassword(), clientIp);
        
        if (!result.isSuccess()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(result.getErrorMessage()));
        }
        
        // Buscar dados completos do usuário para resposta
        User user = authenticateUserService.execute(request.getEmail(), request.getPassword());
        
        AuthResponse response = AuthResponse.builder()
                .accessToken(result.getAccessToken())
                .refreshToken(result.getRefreshToken())
                .user(mapToUserInfo(user))
                .build();

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

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        // Usar o serviço de autenticação segura para refresh token
        SecureAuthenticationService.AuthenticationResult result = 
            secureAuthenticationService.refreshToken(request.getRefreshToken());
        
        if (!result.isSuccess()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(result.getErrorMessage()));
        }
        
        // Buscar dados completos do usuário para resposta
        String userEmail = jwtService.extractUsername(request.getRefreshToken());
        User user = authenticateUserService.execute(userEmail, ""); // Password não necessária para refresh
        
        AuthResponse response = AuthResponse.builder()
                .accessToken(result.getAccessToken())
                .refreshToken(result.getRefreshToken())
                .user(mapToUserInfo(user))
                .build();
        
        return ResponseEntity.ok(ApiResponse.success("Token renovado com sucesso", response));
    }

    private AuthResponse.UserInfo mapToUserInfo(User user) {
        AuthResponse.UserInfo.UserInfoBuilder builder = AuthResponse.UserInfo.builder()
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
