package com.petconnect.infrastructure.adapter.web.dto;

import com.petconnect.domain.user.entity.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private UUID id;
    private String username;
    private String email;
    private String fullName;
    private UserType userType;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private String nome;
    private String location;
    private String contactNumber;
    private String cnpj;
    private String crmv;
    private String storeType;
    private String businessHours;
    private String guardian;
}
