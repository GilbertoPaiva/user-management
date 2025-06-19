package com.petconnect.infrastructure.security.encryption;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DataEncryptionServiceTest {

    private DataEncryptionService dataEncryptionService;

    @BeforeEach
    void setUp() throws Exception {
        dataEncryptionService = new DataEncryptionService();
        
        Field encryptionKeyField = DataEncryptionService.class.getDeclaredField("encryptionKey");
        encryptionKeyField.setAccessible(true);
        encryptionKeyField.set(dataEncryptionService, "MySecretKey12345");
    }

    @Test
    void shouldEncryptAndDecryptData() {
        String originalData = "sensitive-information@example.com";

        String encryptedData = dataEncryptionService.encryptSensitiveData(originalData);
        String decryptedData = dataEncryptionService.decryptSensitiveData(encryptedData);

        assertNotNull(encryptedData);
        assertNotEquals(originalData, encryptedData);
        assertEquals(originalData, decryptedData);
    }

    @Test
    void shouldReturnNullWhenEncryptingNull() {
        String result = dataEncryptionService.encryptSensitiveData(null);
        assertNull(result);
    }

    @Test
    void shouldReturnNullWhenDecryptingNull() {
        String result = dataEncryptionService.decryptSensitiveData(null);
        assertNull(result);
    }

    @Test
    void shouldReturnEmptyStringWhenEncryptingEmpty() {
        String result = dataEncryptionService.encryptSensitiveData("");
        assertEquals("", result);
    }

    @Test
    void shouldReturnEmptyStringWhenDecryptingEmpty() {
        String result = dataEncryptionService.decryptSensitiveData("");
        assertEquals("", result);
    }

    @Test
    void shouldProduceDifferentEncryptionForSameInput() {
        String originalData = "test@example.com";

        String encrypted1 = dataEncryptionService.encryptSensitiveData(originalData);
        String encrypted2 = dataEncryptionService.encryptSensitiveData(originalData);

        assertNotEquals(encrypted1, encrypted2);
        assertEquals(originalData, dataEncryptionService.decryptSensitiveData(encrypted1));
        assertEquals(originalData, dataEncryptionService.decryptSensitiveData(encrypted2));
    }

    @Test
    void shouldHandleLongStrings() {
        String longString = "a".repeat(1000);

        String encrypted = dataEncryptionService.encryptSensitiveData(longString);
        String decrypted = dataEncryptionService.decryptSensitiveData(encrypted);

        assertEquals(longString, decrypted);
    }

    @Test
    void shouldHandleSpecialCharacters() {
        String specialChars = "!@#$%^&*()_+{}[]|\\:;\"'<>,.?/~`áéíóúàèìòùâêîôûãõçñ";

        String encrypted = dataEncryptionService.encryptSensitiveData(specialChars);
        String decrypted = dataEncryptionService.decryptSensitiveData(encrypted);

        assertEquals(specialChars, decrypted);
    }

    @Test
    void shouldThrowExceptionForInvalidDecryption() {
        String invalidEncryptedData = "invalid-base64-data!!!";

        assertThrows(RuntimeException.class, () -> {
            dataEncryptionService.decryptSensitiveData(invalidEncryptedData);
        });
    }

    @Test
    void shouldHandleUnicodeCharacters() {
        String unicode = "Hello 世界 🌍 €ñáéíóú";

        String encrypted = dataEncryptionService.encryptSensitiveData(unicode);
        String decrypted = dataEncryptionService.decryptSensitiveData(encrypted);

        assertEquals(unicode, decrypted);
    }

    @Test
    void shouldGenerateSecureRandomKey() {
        String key1 = dataEncryptionService.generateSecureKey();
        String key2 = dataEncryptionService.generateSecureKey();

        assertNotNull(key1);
        assertNotNull(key2);
        assertNotEquals(key1, key2);
        assertTrue(key1.length() > 0);
        assertTrue(key2.length() > 0);
    }

    @Test
    void shouldMaintainDataIntegrity() {
        String[] testData = {
            "user@example.com",
            "12345678901",
            "sensitive document content",
            "password123!@#",
            "cnpj:12.345.678/0001-90"
        };

        for (String data : testData) {
            String encrypted = dataEncryptionService.encryptSensitiveData(data);
            String decrypted = dataEncryptionService.decryptSensitiveData(encrypted);
            
            assertEquals(data, decrypted, "Data integrity failed for: " + data);
        }
    }
}
