package com.petconnect.domain.tutor.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.UUID;

class TutorTest {

    @Test
    void shouldCreateTutorWithRequiredFields() {
        // Given
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String nome = "João Silva";
        String location = "São Paulo, SP";
        
        // When
        Tutor tutor = Tutor.builder()
                .id(id)
                .userId(userId)
                .nome(nome)
                .location(location)
                .contactNumber("11987654321")
                .guardian("Maria Silva")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        // Then
        assertNotNull(tutor);
        assertEquals(id, tutor.getId());
        assertEquals(userId, tutor.getUserId());
        assertEquals(nome, tutor.getNome());
        assertEquals(location, tutor.getLocation());
        assertEquals("11987654321", tutor.getContactNumber());
        assertEquals("Maria Silva", tutor.getGuardian());
    }

    @Test
    void shouldUpdateTutorInfo() {
        // Given
        Tutor tutor = Tutor.builder()
                .nome("Nome Antigo")
                .location("Localização Antiga")
                .contactNumber("11111111111")
                .guardian("Guardian Antigo")
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();

        String novoNome = "Novo Nome";
        String novaLocation = "Nova Localização";
        String novoContactNumber = "22222222222";
        String novoGuardian = "Novo Guardian";
        LocalDateTime timeBeforeUpdate = LocalDateTime.now();
        
        // When
        tutor.updateInfo(novoNome, novaLocation, novoContactNumber, novoGuardian);
        
        // Then
        assertEquals(novoNome, tutor.getNome());
        assertEquals(novaLocation, tutor.getLocation());
        assertEquals(novoContactNumber, tutor.getContactNumber());
        assertEquals(novoGuardian, tutor.getGuardian());
        assertTrue(tutor.getUpdatedAt().isAfter(timeBeforeUpdate));
    }

    @Test
    void shouldCheckIfTutorHasCnpj() {
        // Given
        Tutor tutorWithCnpj = Tutor.builder()
                .cnpj("12.345.678/0001-90")
                .build();
        
        Tutor tutorWithoutCnpj = Tutor.builder()
                .cnpj(null)
                .build();
        
        Tutor tutorWithEmptyCnpj = Tutor.builder()
                .cnpj("   ")
                .build();
        
        // When & Then
        assertTrue(tutorWithCnpj.hasCnpj());
        assertFalse(tutorWithoutCnpj.hasCnpj());
        assertFalse(tutorWithEmptyCnpj.hasCnpj());
    }

    @Test
    void shouldCreateTutorWithMinimalData() {
        // Given & When
        Tutor tutor = Tutor.builder()
                .userId(UUID.randomUUID())
                .nome("João")
                .build();
        
        // Then
        assertNotNull(tutor);
        assertNotNull(tutor.getUserId());
        assertEquals("João", tutor.getNome());
        assertNull(tutor.getCnpj());
        assertNull(tutor.getLocation());
        assertNull(tutor.getContactNumber());
        assertNull(tutor.getGuardian());
    }

    @Test
    void shouldHandleOptionalCnpj() {
        // Given
        Tutor tutorPessoaFisica = Tutor.builder()
                .nome("João Silva")
                .cnpj(null)
                .build();
        
        Tutor tutorPessoaJuridica = Tutor.builder()
                .nome("Pet Shop Silva LTDA")
                .cnpj("12.345.678/0001-90")
                .build();
        
        // When & Then
        assertFalse(tutorPessoaFisica.hasCnpj());
        assertTrue(tutorPessoaJuridica.hasCnpj());
    }

    @Test
    void shouldUpdateTimestampOnUpdate() {
        // Given
        LocalDateTime originalTime = LocalDateTime.now().minusHours(1);
        Tutor tutor = Tutor.builder()
                .nome("Nome Original")
                .location("Location Original")
                .contactNumber("11111111111")
                .guardian("Guardian Original")
                .updatedAt(originalTime)
                .build();
        
        // When
        tutor.updateInfo("Nome Atualizado", "Location Atualizada", "22222222222", "Guardian Atualizado");
        
        // Then
        assertTrue(tutor.getUpdatedAt().isAfter(originalTime));
        assertTrue(tutor.getUpdatedAt().isAfter(LocalDateTime.now().minusMinutes(1)));
    }

    @Test
    void shouldMaintainImmutableFieldsOnUpdate() {
        // Given
        UUID originalId = UUID.randomUUID();
        UUID originalUserId = UUID.randomUUID();
        String originalCnpj = "12.345.678/0001-90";
        LocalDateTime originalCreatedAt = LocalDateTime.now().minusDays(1);
        
        Tutor tutor = Tutor.builder()
                .id(originalId)
                .userId(originalUserId)
                .nome("Nome Original")
                .cnpj(originalCnpj)
                .location("Location Original")
                .contactNumber("11111111111")
                .guardian("Guardian Original")
                .createdAt(originalCreatedAt)
                .updatedAt(LocalDateTime.now().minusHours(1))
                .build();
        
        // When
        tutor.updateInfo("Novo Nome", "Nova Location", "22222222222", "Novo Guardian");
        
        // Then
        assertEquals(originalId, tutor.getId());
        assertEquals(originalUserId, tutor.getUserId());
        assertEquals(originalCnpj, tutor.getCnpj());
        assertEquals(originalCreatedAt, tutor.getCreatedAt());
        assertEquals("Novo Nome", tutor.getNome());
        assertEquals("Nova Location", tutor.getLocation());
        assertEquals("22222222222", tutor.getContactNumber());
        assertEquals("Novo Guardian", tutor.getGuardian());
    }

    @Test
    void shouldAcceptNullValuesOnUpdate() {
        // Given
        Tutor tutor = Tutor.builder()
                .nome("Nome Original")
                .location("Location Original")
                .contactNumber("11111111111")
                .guardian("Guardian Original")
                .build();
        
        // When
        tutor.updateInfo("Novo Nome", null, null, null);
        
        // Then
        assertEquals("Novo Nome", tutor.getNome());
        assertNull(tutor.getLocation());
        assertNull(tutor.getContactNumber());
        assertNull(tutor.getGuardian());
    }

    @Test
    void shouldHandleEmptyStringCnpj() {
        // Given
        Tutor tutorEmptyString = Tutor.builder()
                .cnpj("")
                .build();
        
        Tutor tutorWhitespace = Tutor.builder()
                .cnpj("   ")
                .build();
        
        // When & Then
        assertFalse(tutorEmptyString.hasCnpj());
        assertFalse(tutorWhitespace.hasCnpj());
    }

    @Test
    void shouldValidateCnpjWithDifferentFormats() {
        // Given
        String[] validCnpjFormats = {
            "12.345.678/0001-90",
            "12345678000190",
            "12 345 678 0001 90",
            " 12.345.678/0001-90 " // with spaces
        };
        
        for (String cnpj : validCnpjFormats) {
            // When
            Tutor tutor = Tutor.builder()
                    .cnpj(cnpj)
                    .build();
            
            // Then
            assertTrue(tutor.hasCnpj(), "CNPJ format '" + cnpj + "' should be valid");
        }
    }

    @Test
    void shouldHandleSpecialCharactersInFields() {
        // Given
        String nomeComAcentos = "José María González";
        String locationComAcentos = "São Paulo - Área Central";
        String guardianComAcentos = "María José González";
        
        // When
        Tutor tutor = Tutor.builder()
                .nome(nomeComAcentos)
                .location(locationComAcentos)
                .guardian(guardianComAcentos)
                .build();
        
        // Then
        assertEquals(nomeComAcentos, tutor.getNome());
        assertEquals(locationComAcentos, tutor.getLocation());
        assertEquals(guardianComAcentos, tutor.getGuardian());
    }

    @Test
    void shouldHandleLongContactNumbers() {
        // Given
        String longContactNumber = "+55 (11) 98765-4321";
        
        // When
        Tutor tutor = Tutor.builder()
                .contactNumber(longContactNumber)
                .build();
        
        tutor.updateInfo("Nome", "Location", "+55 (11) 12345-6789", "Guardian");
        
        // Then
        assertEquals("+55 (11) 12345-6789", tutor.getContactNumber());
    }
}
