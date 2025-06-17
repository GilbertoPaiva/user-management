package com.petconnect.infrastructure.security.encryption;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Service
@Slf4j
public class DataEncryptionService {
    
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    
    @Value("${app.encryption.key:MySecretKey12345}")
    private String encryptionKey;
    
    private SecretKey getSecretKey() {
        byte[] key = encryptionKey.getBytes(StandardCharsets.UTF_8);

        byte[] keyBytes = new byte[16];
        System.arraycopy(key, 0, keyBytes, 0, Math.min(key.length, keyBytes.length));
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }
    
    /**
     * Criptografa dados sensíveis como CPF, CNPJ, email, etc.
     */
    public String encryptSensitiveData(String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            return plainText;
        }
        
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey());
            byte[] encryptedData = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedData);
        } catch (Exception e) {
            log.error("Erro ao criptografar dados sensíveis", e);
            throw new SecurityException("Falha na criptografia de dados", e);
        }
    }
    
    /**
     * Descriptografa dados sensíveis
     */
    public String decryptSensitiveData(String encryptedText) {
        if (encryptedText == null || encryptedText.isEmpty()) {
            return encryptedText;
        }
        
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey());
            byte[] decryptedData = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
            return new String(decryptedData, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Erro ao descriptografar dados sensíveis", e);
            throw new SecurityException("Falha na descriptografia de dados", e);
        }
    }
    
    /**
     * Gera uma chave de criptografia segura
     */
    public String generateSecureKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
            keyGenerator.init(256, new SecureRandom());
            SecretKey secretKey = keyGenerator.generateKey();
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new SecurityException("Erro ao gerar chave de criptografia", e);
        }
    }
    
    /**
     * Gera um salt aleatório para senhas
     */
    public String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[32];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    
    /**
     * Mascarar dados sensíveis para logs
     */
    public String maskSensitiveData(String data, int visibleChars) {
        if (data == null || data.length() <= visibleChars) {
            return "***";
        }
        
        StringBuilder masked = new StringBuilder();
        masked.append(data.substring(0, visibleChars));
        masked.append("*".repeat(data.length() - visibleChars));
        return masked.toString();
    }
}
