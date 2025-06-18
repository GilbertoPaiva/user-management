package com.petconnect.infrastructure.validation;

import com.petconnect.infrastructure.security.service.SecureAuthenticationService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TemporaryTest {
    
    @Test
    void testEmailValidation() {
        SecureAuthenticationService service = new SecureAuthenticationService(
            null, null, null, null, null, null
        );
        
        System.out.println("Testing email validation:");
        System.out.println("valid email: " + service.isValidEmail("test@example.com"));
        System.out.println("invalid email: " + service.isValidEmail("invalid-email"));
        
        // Test the authenticate method
        SecureAuthenticationService.AuthenticationResult result = 
            service.authenticateUser("invalid-email", "ValidPassword123!", "127.0.0.1");
            
        System.out.println("Authentication result success: " + result.isSuccess());
        System.out.println("Authentication result message: " + result.getErrorMessage());
    }
}
