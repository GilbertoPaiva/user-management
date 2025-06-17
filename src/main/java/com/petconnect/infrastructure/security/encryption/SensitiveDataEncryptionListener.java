package com.petconnect.infrastructure.security.encryption;

import com.petconnect.infrastructure.adapter.persistence.entity.UserJpaEntity;
import com.petconnect.infrastructure.security.interceptor.SecurityInterceptor;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Entity Listener para criptografar dados sensíveis automaticamente
 */
@Component
@Slf4j
public class SensitiveDataEncryptionListener {
    
    private static DataEncryptionService encryptionService;
    
    @Autowired
    public void setEncryptionService(DataEncryptionService encryptionService) {
        SensitiveDataEncryptionListener.encryptionService = encryptionService;
    }
    
    @PrePersist
    @PreUpdate
    public void encryptSensitiveData(Object entity) {
        if (encryptionService == null) {
            log.warn("DataEncryptionService não disponível para criptografia");
            return;
        }
        
        String currentIp = SecurityInterceptor.getCurrentClientIp();
        log.debug("Criptografando dados sensíveis para entidade: {} from IP: {}", 
                 entity.getClass().getSimpleName(), currentIp);
        
        if (entity instanceof UserJpaEntity) {
            encryptUserData((UserJpaEntity) entity);
        }
        // Adicionar outros tipos de entidades conforme necessário
    }
    
    /**
     * Criptografa dados sensíveis do usuário
     */
    private void encryptUserData(UserJpaEntity user) {
        try {
            // Criptografar email se não estiver já criptografado
            if (user.getEmail() != null && !isEncrypted(user.getEmail())) {
                String encryptedEmail = encryptionService.encryptSensitiveData(user.getEmail());
                user.setEmail(encryptedEmail);
                log.debug("Email criptografado para usuário: {}", 
                         encryptionService.maskSensitiveData(user.getUsername(), 3));
            }
            
            // Criptografar campos sensíveis do usuário
            if (user.getContactNumber() != null && !isEncrypted(user.getContactNumber())) {
                user.setContactNumber(encryptionService.encryptSensitiveData(user.getContactNumber()));
            }
            
            // Criptografar CNPJ
            if (user.getCnpj() != null && !isEncrypted(user.getCnpj())) {
                user.setCnpj(encryptionService.encryptSensitiveData(user.getCnpj()));
            }
            
            // Criptografar CRMV
            if (user.getCrmv() != null && !isEncrypted(user.getCrmv())) {
                user.setCrmv(encryptionService.encryptSensitiveData(user.getCrmv()));
            }
            
            // Criptografar respostas das perguntas de segurança
            if (user.getSecurityAnswer1() != null && !isEncrypted(user.getSecurityAnswer1())) {
                user.setSecurityAnswer1(encryptionService.encryptSensitiveData(user.getSecurityAnswer1()));
            }
            
            if (user.getSecurityAnswer2() != null && !isEncrypted(user.getSecurityAnswer2())) {
                user.setSecurityAnswer2(encryptionService.encryptSensitiveData(user.getSecurityAnswer2()));
            }
            
            if (user.getSecurityAnswer3() != null && !isEncrypted(user.getSecurityAnswer3())) {
                user.setSecurityAnswer3(encryptionService.encryptSensitiveData(user.getSecurityAnswer3()));
            }
            
        } catch (Exception e) {
            log.error("Erro ao criptografar dados sensíveis do usuário: {}", 
                     user.getUsername(), e);
            // Não propagar a exceção para não quebrar o fluxo de persistência
        }
    }
    
    /**
     * Verifica se um dado já está criptografado
     * (dados criptografados geralmente são muito maiores que os originais)
     */
    private boolean isEncrypted(String data) {
        if (data == null || data.length() < 50) {
            return false;
        }
        
        // Verificar se contém caracteres que indicam criptografia Base64
        return data.matches("^[A-Za-z0-9+/]*={0,2}$") && data.length() > 50;
    }
}
