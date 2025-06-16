package com.userauth.usermanagement.application.user.usecase;

public interface ResetPasswordUseCase {
    void execute(String email, String securityAnswer, String newPassword);
    String getSecurityQuestion(String email);
}
