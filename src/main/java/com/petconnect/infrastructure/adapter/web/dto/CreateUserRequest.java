package com.petconnect.infrastructure.adapter.web.dto;

import com.petconnect.domain.user.entity.UserType;
import com.petconnect.infrastructure.validation.StrongPassword;
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
    
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ter formato válido")
    private String email;
    
    @NotBlank(message = "Password é obrigatório")
    @StrongPassword
    private String password;
    
    @NotBlank(message = "Nome é obrigatório")
    private String fullName;
    
    @NotNull(message = "Tipo de usuário é obrigatório")
    private UserType userType;

    private String phone;

    private String address;
}
