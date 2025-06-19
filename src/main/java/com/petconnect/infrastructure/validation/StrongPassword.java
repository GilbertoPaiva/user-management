package com.petconnect.infrastructure.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StrongPasswordValidator.class)
@Documented
public @interface StrongPassword {
    String message() default "A senha deve conter pelo menos 8 caracteres, incluindo: uma letra maiúscula, uma minúscula, um número e um caractere especial";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
