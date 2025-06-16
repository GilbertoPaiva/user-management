package com.petconnect.application.user.usecase;

import com.petconnect.domain.user.entity.User;

public interface AuthenticateUserUseCase {
    User execute(String email, String password);
    User createUser(CreateUserCommand command);
}
