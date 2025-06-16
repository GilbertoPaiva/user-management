package com.petconnect.infrastructure.adapter.web.controller;

import com.petconnect.domain.user.entity.UserType;
import com.petconnect.domain.user.port.UserRepositoryPort;
import com.petconnect.infrastructure.adapter.web.dto.UserResponse;
import com.petconnect.infrastructure.adapter.web.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminController {

    private final UserRepositoryPort userRepository;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboard() {
        Map<String, Object> dashboard = new HashMap<>();
        
        Map<String, Long> userCounts = new HashMap<>();
        userCounts.put("ADMIN", userRepository.countByUserType("ADMIN"));
        userCounts.put("LOJISTA", userRepository.countByUserType("LOJISTA"));
        userCounts.put("TUTOR", userRepository.countByUserType("TUTOR"));
        userCounts.put("VETERINARIO", userRepository.countByUserType("VETERINARIO"));
        
        long totalUsers = userCounts.values().stream().mapToLong(Long::longValue).sum();
        
        dashboard.put("userCounts", userCounts);
        dashboard.put("totalUsers", totalUsers);

        return ResponseEntity.ok(ApiResponse.success("Dashboard carregado com sucesso", dashboard));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) UserType userType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<com.petconnect.domain.user.entity.User> users = userRepository.findAll(pageable);
        
        Page<UserResponse> userResponses = users.map(user -> UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .userType(user.getUserType())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .nome(user.getUserProfile() != null ? user.getUserProfile().getNome() : null)
                .location(user.getUserProfile() != null ? user.getUserProfile().getLocation() : null)
                .contactNumber(user.getUserProfile() != null ? user.getUserProfile().getContactNumber() : null)
                .cnpj(user.getUserProfile() != null ? user.getUserProfile().getCnpj() : null)
                .crmv(user.getUserProfile() != null ? user.getUserProfile().getCrmv() : null)
                .storeType(user.getUserProfile() != null ? user.getUserProfile().getStoreType() : null)
                .businessHours(user.getUserProfile() != null ? user.getUserProfile().getBusinessHours() : null)
                .guardian(user.getUserProfile() != null ? user.getUserProfile().getGuardian() : null)
                .build());

        return ResponseEntity.ok(ApiResponse.success(userResponses));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserDetails(@PathVariable UUID id) {
        return userRepository.findById(id)
                .map(user -> {
                    UserResponse response = UserResponse.builder()
                            .id(user.getId())
                            .username(user.getUsername())
                            .email(user.getEmail())
                            .fullName(user.getFullName())
                            .userType(user.getUserType())
                            .active(user.isActive())
                            .createdAt(user.getCreatedAt())
                            .updatedAt(user.getUpdatedAt())
                            .nome(user.getUserProfile() != null ? user.getUserProfile().getNome() : null)
                            .location(user.getUserProfile() != null ? user.getUserProfile().getLocation() : null)
                            .contactNumber(user.getUserProfile() != null ? user.getUserProfile().getContactNumber() : null)
                            .cnpj(user.getUserProfile() != null ? user.getUserProfile().getCnpj() : null)
                            .crmv(user.getUserProfile() != null ? user.getUserProfile().getCrmv() : null)
                            .storeType(user.getUserProfile() != null ? user.getUserProfile().getStoreType() : null)
                            .businessHours(user.getUserProfile() != null ? user.getUserProfile().getBusinessHours() : null)
                            .guardian(user.getUserProfile() != null ? user.getUserProfile().getGuardian() : null)
                            .build();
                    
                    return ResponseEntity.ok(ApiResponse.success("Detalhes do usuário", response));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/users/{id}/toggle-status")
    public ResponseEntity<ApiResponse<UserResponse>> toggleUserStatus(@PathVariable UUID id) {
        return userRepository.findById(id)
                .map(user -> {
                    if (user.isActive()) {
                        user.deactivate();
                    } else {
                        user.activate();
                    }
                    
                    com.petconnect.domain.user.entity.User savedUser = userRepository.save(user);
                    UserResponse response = UserResponse.builder()
                            .id(savedUser.getId())
                            .username(savedUser.getUsername())
                            .email(savedUser.getEmail())
                            .fullName(savedUser.getFullName())
                            .userType(savedUser.getUserType())
                            .active(savedUser.isActive())
                            .createdAt(savedUser.getCreatedAt())
                            .updatedAt(savedUser.getUpdatedAt())
                            .build();
                    
                    String message = savedUser.isActive() ? "Usuário ativado com sucesso" : "Usuário desativado com sucesso";
                    return ResponseEntity.ok(ApiResponse.success(message, response));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
