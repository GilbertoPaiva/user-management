package com.userauth.usermanagement.application.user.usecase;

import com.userauth.usermanagement.domain.user.entity.User;

public interface AuthenticateUserUseCase {
    User execute(String email, String password);
    User createUser(CreateUserCommand command);
}
