package com.petconnect.domain.veterinario.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.UUID;

class VeterinarioTest {

    @Test
    void shouldCreateVeterinarioWithRequiredFields() {
        // Given
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String nome = "Dr. Carlos Silva";
        String crmv = "CRMV-SP 12345";
        String location = "São Paulo, SP";
        
        // When
        Veterinario veterinario = Veterinario.builder()
                .id(id)
                .userId(userId)
                .nome(nome)
                .crmv(crmv)
                .location(location)
                .contactNumber("11987654321")
                .businessHours("Segunda a Sexta: 8h às 18h")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        // Then
        assertNotNull(veterinario);
        assertEquals(id, veterinario.getId());
        assertEquals(userId, veterinario.getUserId());
        assertEquals(nome, veterinario.getNome());
        assertEquals(crmv, veterinario.getCrmv());
        assertEquals(location, veterinario.getLocation());
        assertEquals("11987654321", veterinario.getContactNumber());
        assertEquals("Segunda a Sexta: 8h às 18h", veterinario.getBusinessHours());
    }

    @Test
    void shouldUpdateVeterinarioInfo() {
        // Given
        Veterinario veterinario = Veterinario.builder()
                .nome("Nome Antigo")
                .location("Localização Antiga")
                .contactNumber("11111111111")
                .businessHours("Horário Antigo")
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();

        String novoNome = "Dr. Novo Nome";
        String novaLocation = "Nova Localização";
        String novoContactNumber = "22222222222";
        String novoBusinessHours = "Segunda a Domingo: 24h";
        LocalDateTime timeBeforeUpdate = LocalDateTime.now();
        
        // When
        veterinario.updateInfo(novoNome, novaLocation, novoContactNumber, novoBusinessHours);
        
        // Then
        assertEquals(novoNome, veterinario.getNome());
        assertEquals(novaLocation, veterinario.getLocation());
        assertEquals(novoContactNumber, veterinario.getContactNumber());
        assertEquals(novoBusinessHours, veterinario.getBusinessHours());
        assertTrue(veterinario.getUpdatedAt().isAfter(timeBeforeUpdate));
    }

    @Test
    void shouldValidateValidCrmv() {
        // Given
        Veterinario veterinarioValidCrmv = Veterinario.builder()
                .crmv("CRMV-SP 12345")
                .build();
        
        // When & Then
        assertTrue(veterinarioValidCrmv.isValidCrmv());
    }

    @Test
    void shouldInvalidateNullCrmv() {
        // Given
        Veterinario veterinarioNullCrmv = Veterinario.builder()
                .crmv(null)
                .build();
        
        // When & Then
        assertFalse(veterinarioNullCrmv.isValidCrmv());
    }

    @Test
    void shouldInvalidateEmptyCrmv() {
        // Given
        Veterinario veterinarioEmptyCrmv = Veterinario.builder()
                .crmv("")
                .build();
        
        // When & Then
        assertFalse(veterinarioEmptyCrmv.isValidCrmv());
    }

    @Test
    void shouldInvalidateWhitespaceCrmv() {
        // Given
        Veterinario veterinarioWhitespaceCrmv = Veterinario.builder()
                .crmv("   ")
                .build();
        
        // When & Then
        assertFalse(veterinarioWhitespaceCrmv.isValidCrmv());
    }

    @Test
    void shouldCreateVeterinarioWithMinimalData() {
        // Given & When
        Veterinario veterinario = Veterinario.builder()
                .userId(UUID.randomUUID())
                .nome("Dr. João")
                .crmv("CRMV-RJ 99999")
                .build();
        
        // Then
        assertNotNull(veterinario);
        assertNotNull(veterinario.getUserId());
        assertEquals("Dr. João", veterinario.getNome());
        assertEquals("CRMV-RJ 99999", veterinario.getCrmv());
        assertNull(veterinario.getLocation());
        assertNull(veterinario.getContactNumber());
        assertNull(veterinario.getBusinessHours());
    }

    @Test
    void shouldUpdateTimestampOnUpdate() {
        // Given
        LocalDateTime originalTime = LocalDateTime.now().minusHours(1);
        Veterinario veterinario = Veterinario.builder()
                .nome("Nome Original")
                .location("Location Original")
                .contactNumber("11111111111")
                .businessHours("Horário Original")
                .updatedAt(originalTime)
                .build();
        
        // When
        veterinario.updateInfo("Nome Atualizado", "Location Atualizada", "22222222222", "Horário Atualizado");
        
        // Then
        assertTrue(veterinario.getUpdatedAt().isAfter(originalTime));
        assertTrue(veterinario.getUpdatedAt().isAfter(LocalDateTime.now().minusMinutes(1)));
    }

    @Test
    void shouldMaintainImmutableFieldsOnUpdate() {
        // Given
        UUID originalId = UUID.randomUUID();
        UUID originalUserId = UUID.randomUUID();
        String originalCrmv = "CRMV-SP 12345";
        LocalDateTime originalCreatedAt = LocalDateTime.now().minusDays(1);
        
        Veterinario veterinario = Veterinario.builder()
                .id(originalId)
                .userId(originalUserId)
                .nome("Nome Original")
                .crmv(originalCrmv)
                .location("Location Original")
                .contactNumber("11111111111")
                .businessHours("Horário Original")
                .createdAt(originalCreatedAt)
                .updatedAt(LocalDateTime.now().minusHours(1))
                .build();
        
        // When
        veterinario.updateInfo("Novo Nome", "Nova Location", "22222222222", "Novo Horário");
        
        // Then
        assertEquals(originalId, veterinario.getId());
        assertEquals(originalUserId, veterinario.getUserId());
        assertEquals(originalCrmv, veterinario.getCrmv());
        assertEquals(originalCreatedAt, veterinario.getCreatedAt());
        assertEquals("Novo Nome", veterinario.getNome());
        assertEquals("Nova Location", veterinario.getLocation());
        assertEquals("22222222222", veterinario.getContactNumber());
        assertEquals("Novo Horário", veterinario.getBusinessHours());
    }

    @Test
    void shouldAcceptNullValuesOnUpdate() {
        // Given
        Veterinario veterinario = Veterinario.builder()
                .nome("Nome Original")
                .location("Location Original")
                .contactNumber("11111111111")
                .businessHours("Horário Original")
                .build();
        
        // When
        veterinario.updateInfo("Novo Nome", null, null, null);
        
        // Then
        assertEquals("Novo Nome", veterinario.getNome());
        assertNull(veterinario.getLocation());
        assertNull(veterinario.getContactNumber());
        assertNull(veterinario.getBusinessHours());
    }

    @Test
    void shouldValidateDifferentCrmvFormats() {
        // Given
        String[] validCrmvFormats = {
            "CRMV-SP 12345",
            "CRMV/SP 12345",
            "CRMV-RJ-98765",
            "CRMV MG 55555",
            "12345-CRMV-PR",
            "CRMVs123456"
        };
        
        for (String crmv : validCrmvFormats) {
            // When
            Veterinario veterinario = Veterinario.builder()
                    .crmv(crmv)
                    .build();
            
            // Then
            assertTrue(veterinario.isValidCrmv(), "CRMV format '" + crmv + "' should be valid");
        }
    }

    @Test
    void shouldHandleSpecialCharactersInFields() {
        // Given
        String nomeComTitulo = "Dr. José María González";
        String locationComAcentos = "São Paulo - Área Central";
        String businessHoursDetalhado = "Segunda à Sexta: 8h às 18h\nSábado: 8h às 12h";
        
        // When
        Veterinario veterinario = Veterinario.builder()
                .nome(nomeComTitulo)
                .location(locationComAcentos)
                .businessHours(businessHoursDetalhado)
                .build();
        
        // Then
        assertEquals(nomeComTitulo, veterinario.getNome());
        assertEquals(locationComAcentos, veterinario.getLocation());
        assertEquals(businessHoursDetalhado, veterinario.getBusinessHours());
    }

    @Test
    void shouldHandleLongContactNumbers() {
        // Given
        String longContactNumber = "+55 (11) 98765-4321";
        
        // When
        Veterinario veterinario = Veterinario.builder()
                .contactNumber(longContactNumber)
                .build();
        
        veterinario.updateInfo("Nome", "Location", "+55 (11) 12345-6789", "Horário");
        
        // Then
        assertEquals("+55 (11) 12345-6789", veterinario.getContactNumber());
    }

    @Test
    void shouldHandleComplexBusinessHours() {
        // Given
        String complexBusinessHours = "Segunda a Quinta: 8h às 18h\nSexta: 8h às 17h\nSábado: 8h às 12h\nDomingo: Plantão 24h\nFeriados: Consultar";
        
        // When
        Veterinario veterinario = Veterinario.builder()
                .businessHours(complexBusinessHours)
                .build();
        
        // Then
        assertEquals(complexBusinessHours, veterinario.getBusinessHours());
    }

    @Test
    void shouldAllowUpdateWithSameValues() {
        // Given
        Veterinario veterinario = Veterinario.builder()
                .nome("Dr. Silva")
                .location("São Paulo")
                .contactNumber("11999999999")
                .businessHours("8h às 18h")
                .updatedAt(LocalDateTime.now().minusHours(1))
                .build();
        
        LocalDateTime timeBeforeUpdate = LocalDateTime.now();
        
        // When - Update with same values
        veterinario.updateInfo("Dr. Silva", "São Paulo", "11999999999", "8h às 18h");
        
        // Then - Should still update the timestamp
        assertTrue(veterinario.getUpdatedAt().isAfter(timeBeforeUpdate));
        assertEquals("Dr. Silva", veterinario.getNome());
        assertEquals("São Paulo", veterinario.getLocation());
    }

    @Test
    void shouldHandleCrmvWithSpacesAndSpecialCharacters() {
        // Given
        String crmvWithSpaces = " CRMV-SP 12345 ";
        
        // When
        Veterinario veterinario = Veterinario.builder()
                .crmv(crmvWithSpaces)
                .build();
        
        // Then
        assertTrue(veterinario.isValidCrmv());
        assertEquals(crmvWithSpaces, veterinario.getCrmv());
    }

    @Test
    void shouldMaintainDataIntegrityAfterMultipleUpdates() {
        // Given
        UUID originalId = UUID.randomUUID();
        UUID originalUserId = UUID.randomUUID();
        String originalCrmv = "CRMV-SP 12345";
        
        Veterinario veterinario = Veterinario.builder()
                .id(originalId)
                .userId(originalUserId)
                .crmv(originalCrmv)
                .nome("Dr. Original")
                .location("Local Original")
                .contactNumber("11111111111")
                .businessHours("Horário Original")
                .build();
        
        // When - Multiple updates
        veterinario.updateInfo("Dr. Primeira Mudança", "Local 1", "22222222222", "Horário 1");
        veterinario.updateInfo("Dr. Segunda Mudança", "Local 2", "33333333333", "Horário 2");
        veterinario.updateInfo("Dr. Terceira Mudança", "Local 3", "44444444444", "Horário 3");
        
        // Then - Immutable fields should remain unchanged
        assertEquals(originalId, veterinario.getId());
        assertEquals(originalUserId, veterinario.getUserId());
        assertEquals(originalCrmv, veterinario.getCrmv());
        
        // And mutable fields should have the latest values
        assertEquals("Dr. Terceira Mudança", veterinario.getNome());
        assertEquals("Local 3", veterinario.getLocation());
        assertEquals("44444444444", veterinario.getContactNumber());
        assertEquals("Horário 3", veterinario.getBusinessHours());
    }
}
