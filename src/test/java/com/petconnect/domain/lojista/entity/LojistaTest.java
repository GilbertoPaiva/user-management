package com.petconnect.domain.lojista.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.UUID;

class LojistaTest {

    @Test
    void shouldCreateLojistaWithRequiredFields() {

        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String nome = "Pet Shop Silva";
        String cnpj = "12.345.678/0001-90";
        String location = "São Paulo, SP";
        StoreType storeType = StoreType.LOCAL;
        

        Lojista lojista = Lojista.builder()
                .id(id)
                .userId(userId)
                .nome(nome)
                .cnpj(cnpj)
                .location(location)
                .contactNumber("11987654321")
                .storeType(storeType)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        

        assertNotNull(lojista);
        assertEquals(id, lojista.getId());
        assertEquals(userId, lojista.getUserId());
        assertEquals(nome, lojista.getNome());
        assertEquals(cnpj, lojista.getCnpj());
        assertEquals(location, lojista.getLocation());
        assertEquals("11987654321", lojista.getContactNumber());
        assertEquals(storeType, lojista.getStoreType());
    }

    @Test
    void shouldUpdateLojistaInfo() {

        Lojista lojista = Lojista.builder()
                .nome("Nome Antigo")
                .location("Localização Antiga")
                .contactNumber("11111111111")
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();

        String novoNome = "Pet Shop Novo";
        String novaLocation = "Rio de Janeiro, RJ";
        String novoContactNumber = "22222222222";
        LocalDateTime timeBeforeUpdate = LocalDateTime.now();
        

        lojista.updateInfo(novoNome, novaLocation, novoContactNumber);
        

        assertEquals(novoNome, lojista.getNome());
        assertEquals(novaLocation, lojista.getLocation());
        assertEquals(novoContactNumber, lojista.getContactNumber());
        assertTrue(lojista.getUpdatedAt().isAfter(timeBeforeUpdate));
    }

    @Test
    void shouldCreateLojistaWithVirtualStoreType() {
        Lojista lojistaVirtual = Lojista.builder()
                .nome("E-commerce Pet")
                .storeType(StoreType.VIRTUAL)
                .build();
        

        assertNotNull(lojistaVirtual);
        assertEquals(StoreType.VIRTUAL, lojistaVirtual.getStoreType());
    }

    @Test
    void shouldCreateLojistaWithLocalStoreType() {
        Lojista lojistaLocal = Lojista.builder()
                .nome("Pet Shop Local")
                .storeType(StoreType.LOCAL)
                .build();
        

        assertNotNull(lojistaLocal);
        assertEquals(StoreType.LOCAL, lojistaLocal.getStoreType());
    }

    @Test
    void shouldCreateLojistaWithMinimalData() {
        Lojista lojista = Lojista.builder()
                .userId(UUID.randomUUID())
                .nome("Pet Shop Básico")
                .storeType(StoreType.LOCAL)
                .build();
        

        assertNotNull(lojista);
        assertNotNull(lojista.getUserId());
        assertEquals("Pet Shop Básico", lojista.getNome());
        assertEquals(StoreType.LOCAL, lojista.getStoreType());
        assertNull(lojista.getCnpj());
        assertNull(lojista.getLocation());
        assertNull(lojista.getContactNumber());
    }

    @Test
    void shouldUpdateTimestampOnUpdate() {

        LocalDateTime originalTime = LocalDateTime.now().minusHours(1);
        Lojista lojista = Lojista.builder()
                .nome("Nome Original")
                .location("Location Original")
                .contactNumber("11111111111")
                .updatedAt(originalTime)
                .build();
        

        lojista.updateInfo("Nome Atualizado", "Location Atualizada", "22222222222");
        

        assertTrue(lojista.getUpdatedAt().isAfter(originalTime));
        assertTrue(lojista.getUpdatedAt().isAfter(LocalDateTime.now().minusMinutes(1)));
    }

    @Test
    void shouldMaintainImmutableFieldsOnUpdate() {

        UUID originalId = UUID.randomUUID();
        UUID originalUserId = UUID.randomUUID();
        String originalCnpj = "12.345.678/0001-90";
        StoreType originalStoreType = StoreType.VIRTUAL;
        LocalDateTime originalCreatedAt = LocalDateTime.now().minusDays(1);
        
        Lojista lojista = Lojista.builder()
                .id(originalId)
                .userId(originalUserId)
                .nome("Nome Original")
                .cnpj(originalCnpj)
                .location("Location Original")
                .contactNumber("11111111111")
                .storeType(originalStoreType)
                .createdAt(originalCreatedAt)
                .updatedAt(LocalDateTime.now().minusHours(1))
                .build();
        

        lojista.updateInfo("Novo Nome", "Nova Location", "22222222222");
        

        assertEquals(originalId, lojista.getId());
        assertEquals(originalUserId, lojista.getUserId());
        assertEquals(originalCnpj, lojista.getCnpj());
        assertEquals(originalStoreType, lojista.getStoreType());
        assertEquals(originalCreatedAt, lojista.getCreatedAt());
        assertEquals("Novo Nome", lojista.getNome());
        assertEquals("Nova Location", lojista.getLocation());
        assertEquals("22222222222", lojista.getContactNumber());
    }

    @Test
    void shouldAcceptNullValuesOnUpdate() {

        Lojista lojista = Lojista.builder()
                .nome("Nome Original")
                .location("Location Original")
                .contactNumber("11111111111")
                .build();
        

        lojista.updateInfo("Novo Nome", null, null);
        

        assertEquals("Novo Nome", lojista.getNome());
        assertNull(lojista.getLocation());
        assertNull(lojista.getContactNumber());
    }

    @Test
    void shouldHandleDifferentCnpjFormats() {

        String[] validCnpjFormats = {
            "12.345.678/0001-90",
            "12345678000190",
            "12 345 678 0001 90",
            " 12.345.678/0001-90 " // with spaces
        };
        
        for (String cnpj : validCnpjFormats) {
    
            Lojista lojista = Lojista.builder()
                    .cnpj(cnpj)
                    .build();
            
    
            assertEquals(cnpj, lojista.getCnpj());
        }
    }

    @Test
    void shouldHandleSpecialCharactersInFields() {

        String nomeComCaracteresEspeciais = "Pet Shop & Cia Ltda.";
        String locationComAcentos = "São Paulo - Área Central";
        

        Lojista lojista = Lojista.builder()
                .nome(nomeComCaracteresEspeciais)
                .location(locationComAcentos)
                .build();
        

        assertEquals(nomeComCaracteresEspeciais, lojista.getNome());
        assertEquals(locationComAcentos, lojista.getLocation());
    }

    @Test
    void shouldHandleLongContactNumbers() {

        String longContactNumber = "+55 (11) 98765-4321";
        

        Lojista lojista = Lojista.builder()
                .contactNumber(longContactNumber)
                .build();
        
        lojista.updateInfo("Nome", "Location", "+55 (11) 12345-6789");
        

        assertEquals("+55 (11) 12345-6789", lojista.getContactNumber());
    }

    @Test
    void shouldDistinguishBetweenStoreTypes() {

        Lojista lojistaVirtual = Lojista.builder()
                .nome("E-commerce Pet")
                .storeType(StoreType.VIRTUAL)
                .build();
        
        Lojista lojistaLocal = Lojista.builder()
                .nome("Pet Shop Físico")
                .storeType(StoreType.LOCAL)
                .build();
        

        assertNotEquals(lojistaVirtual.getStoreType(), lojistaLocal.getStoreType());
        assertEquals(StoreType.VIRTUAL, lojistaVirtual.getStoreType());
        assertEquals(StoreType.LOCAL, lojistaLocal.getStoreType());
    }

    @Test
    void shouldAllowUpdateWithSameValues() {

        Lojista lojista = Lojista.builder()
                .nome("Pet Shop Silva")
                .location("São Paulo")
                .contactNumber("11999999999")
                .updatedAt(LocalDateTime.now().minusHours(1))
                .build();
        
        LocalDateTime timeBeforeUpdate = LocalDateTime.now();
        
        // Update with same values
        lojista.updateInfo("Pet Shop Silva", "São Paulo", "11999999999");
        
        // Should still update the timestamp
        assertTrue(lojista.getUpdatedAt().isAfter(timeBeforeUpdate));
        assertEquals("Pet Shop Silva", lojista.getNome());
        assertEquals("São Paulo", lojista.getLocation());
    }

    @Test
    void shouldMaintainDataIntegrityAfterMultipleUpdates() {

        UUID originalId = UUID.randomUUID();
        UUID originalUserId = UUID.randomUUID();
        String originalCnpj = "12.345.678/0001-90";
        StoreType originalStoreType = StoreType.LOCAL;
        
        Lojista lojista = Lojista.builder()
                .id(originalId)
                .userId(originalUserId)
                .cnpj(originalCnpj)
                .storeType(originalStoreType)
                .nome("Pet Shop Original")
                .location("Local Original")
                .contactNumber("11111111111")
                .build();
        
        // Multiple updates
        lojista.updateInfo("Pet Shop Primeira Mudança", "Local 1", "22222222222");
        lojista.updateInfo("Pet Shop Segunda Mudança", "Local 2", "33333333333");
        lojista.updateInfo("Pet Shop Terceira Mudança", "Local 3", "44444444444");
        
        // Immutable fields should remain unchanged
        assertEquals(originalId, lojista.getId());
        assertEquals(originalUserId, lojista.getUserId());
        assertEquals(originalCnpj, lojista.getCnpj());
        assertEquals(originalStoreType, lojista.getStoreType());
        
        // And mutable fields should have the latest values
        assertEquals("Pet Shop Terceira Mudança", lojista.getNome());
        assertEquals("Local 3", lojista.getLocation());
        assertEquals("44444444444", lojista.getContactNumber());
    }

    @Test
    void shouldHandleNullCnpj() {
        Lojista lojista = Lojista.builder()
                .nome("Pet Shop Sem CNPJ")
                .cnpj(null)
                .storeType(StoreType.LOCAL)
                .build();
        

        assertNotNull(lojista);
        assertNull(lojista.getCnpj());
        assertEquals("Pet Shop Sem CNPJ", lojista.getNome());
    }

    @Test
    void shouldValidateStoreTypeEnumValues() {

        StoreType[] allStoreTypes = StoreType.values();
        

        assertEquals(2, allStoreTypes.length);
        assertTrue(java.util.Arrays.asList(allStoreTypes).contains(StoreType.VIRTUAL));
        assertTrue(java.util.Arrays.asList(allStoreTypes).contains(StoreType.LOCAL));
    }

    @Test
    void shouldCreateLojistaWithAllStoreTypes() {
        for (StoreType storeType : StoreType.values()) {
            Lojista lojista = Lojista.builder()
                    .nome("Pet Shop " + storeType.name())
                    .storeType(storeType)
                    .build();
            
    
            assertNotNull(lojista);
            assertEquals(storeType, lojista.getStoreType());
        }
    }
}
