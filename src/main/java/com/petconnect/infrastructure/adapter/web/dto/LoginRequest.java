package com.petconnect.infrastructure.adapter.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data // <-- A anotação mais importante!
public class LoginRequest {
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ter um formato válido")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    private String password;
}