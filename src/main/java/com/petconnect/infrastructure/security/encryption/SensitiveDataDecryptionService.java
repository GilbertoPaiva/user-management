package com.petconnect.infrastructure.security.encryption;

import com.petconnect.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Serviço para descriptografar dados sensíveis quando necessário
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SensitiveDataDecryptionService {
    
    private final DataEncryptionService encryptionService;
    
    /**
     * Descriptografa dados sensíveis do usuário para exibição
     */
    public User decryptUserSensitiveData(User user) {
        if (user == null) {
            return null;
        }
        
        try {

            User decryptedUser = User.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(decryptIfEncrypted(user.getEmail()))
                    .password(user.getPassword())
                    .fullName(user.getFullName())
                    .userType(user.getUserType())
                    .active(user.isActive())
                    .roles(user.getRoles())
                    .createdAt(user.getCreatedAt())
                    .updatedAt(user.getUpdatedAt())
                    .build();
            

            if (user.getUserProfile() != null) {
                var profile = user.getUserProfile();
                var decryptedProfile = com.petconnect.domain.user.entity.UserProfile.builder()
                        .nome(profile.getNome())
                        .location(profile.getLocation())
                        .contactNumber(decryptIfEncrypted(profile.getContactNumber()))
                        .cnpj(decryptIfEncrypted(profile.getCnpj()))
                        .crmv(decryptIfEncrypted(profile.getCrmv()))
                        .storeType(profile.getStoreType())
                        .businessHours(profile.getBusinessHours())
                        .guardian(profile.getGuardian())
                        .build();
                
                decryptedUser.setUserProfile(decryptedProfile);
            }
            

            if (user.getSecurityQuestions() != null) {
                var secQuestions = user.getSecurityQuestions();
                var decryptedSecQuestions = com.petconnect.domain.user.entity.SecurityQuestions.builder()
                        .question1(secQuestions.getQuestion1())
                        .answer1(decryptIfEncrypted(secQuestions.getAnswer1()))
                        .question2(secQuestions.getQuestion2())
                        .answer2(decryptIfEncrypted(secQuestions.getAnswer2()))
                        .question3(secQuestions.getQuestion3())
                        .answer3(decryptIfEncrypted(secQuestions.getAnswer3()))
                        .build();
                
                decryptedUser.setSecurityQuestions(decryptedSecQuestions);
            }
            
            return decryptedUser;
            
        } catch (Exception e) {
            log.error("Erro ao descriptografar dados sensíveis do usuário: {}", 
                     encryptionService.maskSensitiveData(user.getUsername(), 3), e);
            return user;
        }
    }
    
    /**
     * Descriptografa apenas o email do usuário (para autenticação)
     */
    public String decryptUserEmail(String encryptedEmail) {
        return decryptIfEncrypted(encryptedEmail);
    }
    
    /**
     * Descriptografa dados sensíveis para comparação (ex: perguntas de segurança)
     */
    public boolean compareEncryptedData(String plainText, String encryptedData) {
        try {
            if (encryptedData == null || plainText == null) {
                return false;
            }
            

            if (!isEncrypted(encryptedData)) {
                return plainText.equals(encryptedData);
            }
            
            String decryptedData = encryptionService.decryptSensitiveData(encryptedData);
            return plainText.equals(decryptedData);
            
        } catch (Exception e) {
            log.error("Erro ao comparar dados criptografados", e);
            return false;
        }
    }
    
    /**
     * Criptografa um dado sensível
     */
    public String encryptSensitiveData(String plainData) {
        if (plainData == null || plainData.trim().isEmpty()) {
            return plainData;
        }
        
        try {
            return encryptionService.encryptSensitiveData(plainData);
        } catch (Exception e) {
            log.error("Erro ao criptografar dado sensível", e);
            return plainData;
        }
    }
    
    /**
     * Descriptografa um dado se estiver criptografado
     */
    private String decryptIfEncrypted(String data) {
        if (data == null || !isEncrypted(data)) {
            return data;
        }
        
        try {
            return encryptionService.decryptSensitiveData(data);
        } catch (Exception e) {
            log.warn("Erro ao descriptografar dado sensível, retornando original", e);
            return data;
        }
    }
    
    /**
     * Verifica se um dado está criptografado
     */
    private boolean isEncrypted(String data) {
        if (data == null || data.length() < 50) {
            return false;
        }
        

        return data.matches("^[A-Za-z0-9+/]*={0,2}$") && data.length() > 50;
    }
    
    /**
     * Máscara dados sensíveis para logs
     */
    public String maskSensitiveData(String data, int visibleChars) {
        return encryptionService.maskSensitiveData(data, visibleChars);
    }
}
