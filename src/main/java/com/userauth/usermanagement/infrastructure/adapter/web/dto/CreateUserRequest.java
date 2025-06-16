package com.userauth.usermanagement.infrastructure.adapter.web.dto;

import com.userauth.usermanagement.domain.user.entity.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {
    
    @NotBlank(message = "Username é obrigatório")
    private String username;
    
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ter formato válido")
    private String email;
    
    @NotBlank(message = "Password é obrigatório")
    private String password;
    
    @NotBlank(message = "Nome completo é obrigatório")
    private String fullName;
    
    @NotNull(message = "Tipo de usuário é obrigatório")
    private UserType userType;
    
    private String securityQuestion1;
    private String securityAnswer1;
    private String securityQuestion2;
    private String securityAnswer2;
    private String securityQuestion3;
    private String securityAnswer3;
    
    private String nome;
    private String location;
    private String contactNumber;
    private String cnpj;
    private String crmv;
    private String storeType;
    private String businessHours;
    private String guardian;
}
