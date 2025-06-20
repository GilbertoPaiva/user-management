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
                .localizacao(location)
                .numeroContato("11987654321")
                .tipoLoja(storeType)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        

        assertNotNull(lojista);
        assertEquals(id, lojista.getId());
        assertEquals(userId, lojista.getUserId());
        assertEquals(nome, lojista.getNome());
        assertEquals(cnpj, lojista.getCnpj());
        assertEquals(location, lojista.getLocalizacao());
        assertEquals("11987654321", lojista.getNumeroContato());
        assertEquals(storeType, lojista.getTipoLoja());
    }

    @Test
    void shouldUpdateLojistaInfo() {

        Lojista lojista = Lojista.builder()
                .nome("Nome Antigo")
                .localizacao("Localização Antiga")
                .numeroContato("11111111111")
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();

        String novoNome = "Pet Shop Novo";
        String novaLocation = "Rio de Janeiro, RJ";
        String novoContactNumber = "22222222222";
        LocalDateTime timeBeforeUpdate = LocalDateTime.now();
        

        lojista.updateInfo(novoNome, novaLocation, novoContactNumber);
        

        assertEquals(novoNome, lojista.getNome());
        assertEquals(novaLocation, lojista.getLocalizacao());
        assertEquals(novoContactNumber, lojista.getNumeroContato());
        assertTrue(lojista.getUpdatedAt().isAfter(timeBeforeUpdate));
    }

    @Test
    void shouldCreateLojistaWithVirtualStoreType() {
        Lojista lojistaVirtual = Lojista.builder()
                .nome("E-commerce Pet")
                .tipoLoja(StoreType.VIRTUAL)
                .build();
        

        assertNotNull(lojistaVirtual);
        assertEquals(StoreType.VIRTUAL, lojistaVirtual.getTipoLoja());
    }

    @Test
    void shouldCreateLojistaWithLocalStoreType() {
        Lojista lojistaLocal = Lojista.builder()
                .nome("Pet Shop Local")
                .tipoLoja(StoreType.LOCAL)
                .build();
        

        assertNotNull(lojistaLocal);
        assertEquals(StoreType.LOCAL, lojistaLocal.getTipoLoja());
    }

    @Test
    void shouldCreateLojistaWithMinimalData() {
        Lojista lojista = Lojista.builder()
                .userId(UUID.randomUUID())
                .nome("Pet Shop Básico")
                .tipoLoja(StoreType.LOCAL)
                .build();
        

        assertNotNull(lojista);
        assertNotNull(lojista.getUserId());
        assertEquals("Pet Shop Básico", lojista.getNome());
        assertEquals(StoreType.LOCAL, lojista.getTipoLoja());
        assertNull(lojista.getCnpj());
        assertNull(lojista.getLocalizacao());
        assertNull(lojista.getNumeroContato());
    }

    @Test
    void shouldUpdateTimestampOnUpdate() {

        LocalDateTime originalTime = LocalDateTime.now().minusHours(1);
        Lojista lojista = Lojista.builder()
                .nome("Nome Original")
                .localizacao("Location Original")
                .numeroContato("11111111111")
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
                .localizacao("Location Original")
                .numeroContato("11111111111")
                .tipoLoja(originalStoreType)
                .createdAt(originalCreatedAt)
                .updatedAt(LocalDateTime.now().minusHours(1))
                .build();
        

        lojista.updateInfo("Novo Nome", "Nova Location", "22222222222");
        

        assertEquals(originalId, lojista.getId());
        assertEquals(originalUserId, lojista.getUserId());
        assertEquals(originalCnpj, lojista.getCnpj());
        assertEquals(originalStoreType, lojista.getTipoLoja());
        assertEquals(originalCreatedAt, lojista.getCreatedAt());
        assertEquals("Novo Nome", lojista.getNome());
        assertEquals("Nova Location", lojista.getLocalizacao());
        assertEquals("22222222222", lojista.getNumeroContato());
    }

    @Test
    void shouldAcceptNullValuesOnUpdate() {

        Lojista lojista = Lojista.builder()
                .nome("Nome Original")
                .localizacao("Location Original")
                .numeroContato("11111111111")
                .build();
        

        lojista.updateInfo("Novo Nome", null, null);
        

        assertEquals("Novo Nome", lojista.getNome());
        assertNull(lojista.getLocalizacao());
        assertNull(lojista.getNumeroContato());
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
                .localizacao(locationComAcentos)
                .build();
        

        assertEquals(nomeComCaracteresEspeciais, lojista.getNome());
        assertEquals(locationComAcentos, lojista.getLocalizacao());
    }

    @Test
    void shouldHandleLongContactNumbers() {

        String longContactNumber = "+55 (11) 98765-4321";
        

        Lojista lojista = Lojista.builder()
                .numeroContato(longContactNumber)
                .build();
        
        lojista.updateInfo("Nome", "Location", "+55 (11) 12345-6789");
        

        assertEquals("+55 (11) 12345-6789", lojista.getNumeroContato());
    }

    @Test
    void shouldDistinguishBetweenStoreTypes() {

        Lojista lojistaVirtual = Lojista.builder()
                .nome("E-commerce Pet")
                .tipoLoja(StoreType.VIRTUAL)
                .build();
        
        Lojista lojistaLocal = Lojista.builder()
                .nome("Pet Shop Físico")
                .tipoLoja(StoreType.LOCAL)
                .build();
        

        assertNotEquals(lojistaVirtual.getTipoLoja(), lojistaLocal.getTipoLoja());
        assertEquals(StoreType.VIRTUAL, lojistaVirtual.getTipoLoja());
        assertEquals(StoreType.LOCAL, lojistaLocal.getTipoLoja());
    }

    @Test
    void shouldAllowUpdateWithSameValues() {

        Lojista lojista = Lojista.builder()
                .nome("Pet Shop Silva")
                .localizacao("São Paulo")
                .numeroContato("11999999999")
                .updatedAt(LocalDateTime.now().minusHours(1))
                .build();
        
        LocalDateTime timeBeforeUpdate = LocalDateTime.now();
        
        // Update with same values
        lojista.updateInfo("Pet Shop Silva", "São Paulo", "11999999999");
        
        // Should still update the timestamp
        assertTrue(lojista.getUpdatedAt().isAfter(timeBeforeUpdate));
        assertEquals("Pet Shop Silva", lojista.getNome());
        assertEquals("São Paulo", lojista.getLocalizacao());
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
                .tipoLoja(originalStoreType)
                .nome("Pet Shop Original")
                .localizacao("Local Original")
                .numeroContato("11111111111")
                .build();
        
        // Multiple updates
        lojista.updateInfo("Pet Shop Primeira Mudança", "Local 1", "22222222222");
        lojista.updateInfo("Pet Shop Segunda Mudança", "Local 2", "33333333333");
        lojista.updateInfo("Pet Shop Terceira Mudança", "Local 3", "44444444444");
        
        // Immutable fields should remain unchanged
        assertEquals(originalId, lojista.getId());
        assertEquals(originalUserId, lojista.getUserId());
        assertEquals(originalCnpj, lojista.getCnpj());
        assertEquals(originalStoreType, lojista.getTipoLoja());
        
        // And mutable fields should have the latest values
        assertEquals("Pet Shop Terceira Mudança", lojista.getNome());
        assertEquals("Local 3", lojista.getLocalizacao());
        assertEquals("44444444444", lojista.getNumeroContato());
    }

    @Test
    void shouldHandleNullCnpj() {
        Lojista lojista = Lojista.builder()
                .nome("Pet Shop Sem CNPJ")
                .cnpj(null)
                .tipoLoja(StoreType.LOCAL)
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
                    .tipoLoja(storeType)
                    .build();
            
    
            assertNotNull(lojista);
            assertEquals(storeType, lojista.getTipoLoja());
        }
    }
}
