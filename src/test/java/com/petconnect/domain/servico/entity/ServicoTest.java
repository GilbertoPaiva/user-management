package com.petconnect.domain.servico.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

class ServicoTest {

    @Test
    void shouldCreateServicoWithRequiredFields() {
        // Given
        UUID id = UUID.randomUUID();
        UUID veterinarioId = UUID.randomUUID();
        String nome = "Consulta Veterinária";
        BigDecimal price = new BigDecimal("80.00");
        
        // When
        Servico servico = Servico.builder()
                .id(id)
                .veterinarioId(veterinarioId)
                .nome(nome)
                .description("Consulta clínica geral")
                .price(price)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        // Then
        assertNotNull(servico);
        assertEquals(id, servico.getId());
        assertEquals(veterinarioId, servico.getVeterinarioId());
        assertEquals(nome, servico.getNome());
        assertEquals(price, servico.getPrice());
        assertEquals("Consulta clínica geral", servico.getDescription());
    }

    @Test
    void shouldUpdateServicoInfo() {
        // Given
        Servico servico = Servico.builder()
                .nome("Nome Antigo")
                .description("Descrição Antiga")
                .price(new BigDecimal("50.00"))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();

        String novoNome = "Novo Nome";
        String novaDescricao = "Nova Descrição";
        BigDecimal novoPreco = new BigDecimal("75.50");
        LocalDateTime timeBeforeUpdate = LocalDateTime.now();
        
        // When
        servico.updateInfo(novoNome, novaDescricao, novoPreco);
        
        // Then
        assertEquals(novoNome, servico.getNome());
        assertEquals(novaDescricao, servico.getDescription());
        assertEquals(novoPreco, servico.getPrice());
        assertTrue(servico.getUpdatedAt().isAfter(timeBeforeUpdate));
    }

    @Test
    void shouldValidateValidPrice() {
        // Given
        Servico servicoValidPrice = Servico.builder()
                .price(new BigDecimal("100.50"))
                .build();
        
        // When & Then
        assertTrue(servicoValidPrice.isValidPrice());
    }

    @Test
    void shouldInvalidateZeroPrice() {
        // Given
        Servico servicoZeroPrice = Servico.builder()
                .price(BigDecimal.ZERO)
                .build();
        
        // When & Then
        assertFalse(servicoZeroPrice.isValidPrice());
    }

    @Test
    void shouldInvalidateNegativePrice() {
        // Given
        Servico servicoNegativePrice = Servico.builder()
                .price(new BigDecimal("-10.00"))
                .build();
        
        // When & Then
        assertFalse(servicoNegativePrice.isValidPrice());
    }

    @Test
    void shouldInvalidateNullPrice() {
        // Given
        Servico servicoNullPrice = Servico.builder()
                .price(null)
                .build();
        
        // When & Then
        assertFalse(servicoNullPrice.isValidPrice());
    }

    @Test
    void shouldCreateServicoWithMinimalData() {
        // Given & When
        Servico servico = Servico.builder()
                .veterinarioId(UUID.randomUUID())
                .nome("Serviço Básico")
                .price(new BigDecimal("25.00"))
                .build();
        
        // Then
        assertNotNull(servico);
        assertNotNull(servico.getVeterinarioId());
        assertEquals("Serviço Básico", servico.getNome());
        assertEquals(new BigDecimal("25.00"), servico.getPrice());
        assertNull(servico.getDescription());
        assertNull(servico.getCreatedAt());
        assertNull(servico.getUpdatedAt());
    }

    @Test
    void shouldHandleDecimalPrices() {
        // Given
        BigDecimal[] validPrices = {
            new BigDecimal("0.01"),
            new BigDecimal("99.99"),
            new BigDecimal("1000.00"),
            new BigDecimal("50.5"),
            new BigDecimal("25")
        };
        
        for (BigDecimal price : validPrices) {
            // When
            Servico servico = Servico.builder()
                    .price(price)
                    .build();
            
            // Then
            assertTrue(servico.isValidPrice(), "Price " + price + " should be valid");
        }
    }

    @Test
    void shouldUpdateTimestampOnUpdate() {
        // Given
        LocalDateTime originalTime = LocalDateTime.now().minusHours(1);
        Servico servico = Servico.builder()
                .nome("Nome Original")
                .description("Descrição Original")
                .price(new BigDecimal("30.00"))
                .updatedAt(originalTime)
                .build();
        
        // When
        servico.updateInfo("Nome Atualizado", "Descrição Atualizada", new BigDecimal("35.00"));
        
        // Then
        assertTrue(servico.getUpdatedAt().isAfter(originalTime));
        assertTrue(servico.getUpdatedAt().isAfter(LocalDateTime.now().minusMinutes(1)));
    }

    @Test
    void shouldMaintainImmutableFieldsOnUpdate() {
        // Given
        UUID originalId = UUID.randomUUID();
        UUID originalVeterinarioId = UUID.randomUUID();
        LocalDateTime originalCreatedAt = LocalDateTime.now().minusDays(1);
        
        Servico servico = Servico.builder()
                .id(originalId)
                .veterinarioId(originalVeterinarioId)
                .nome("Nome Original")
                .description("Descrição Original")
                .price(new BigDecimal("30.00"))
                .createdAt(originalCreatedAt)
                .updatedAt(LocalDateTime.now().minusHours(1))
                .build();
        
        // When
        servico.updateInfo("Novo Nome", "Nova Descrição", new BigDecimal("40.00"));
        
        // Then
        assertEquals(originalId, servico.getId());
        assertEquals(originalVeterinarioId, servico.getVeterinarioId());
        assertEquals(originalCreatedAt, servico.getCreatedAt());
        assertEquals("Novo Nome", servico.getNome());
        assertEquals("Nova Descrição", servico.getDescription());
        assertEquals(new BigDecimal("40.00"), servico.getPrice());
    }

    @Test
    void shouldAcceptNullDescriptionOnUpdate() {
        // Given
        Servico servico = Servico.builder()
                .nome("Nome Original")
                .description("Descrição Original")
                .price(new BigDecimal("30.00"))
                .build();
        
        // When
        servico.updateInfo("Novo Nome", null, new BigDecimal("35.00"));
        
        // Then
        assertEquals("Novo Nome", servico.getNome());
        assertNull(servico.getDescription());
        assertEquals(new BigDecimal("35.00"), servico.getPrice());
    }

    @Test
    void shouldComparePricesCorrectly() {
        // Given
        Servico servico1 = Servico.builder()
                .price(new BigDecimal("50.00"))
                .build();
        
        Servico servico2 = Servico.builder()
                .price(new BigDecimal("50.0"))
                .build();
        
        Servico servico3 = Servico.builder()
                .price(new BigDecimal("75.00"))
                .build();
        
        // When & Then
        assertEquals(0, servico1.getPrice().compareTo(servico2.getPrice()));
        assertTrue(servico3.getPrice().compareTo(servico1.getPrice()) > 0);
        assertTrue(servico1.getPrice().compareTo(servico3.getPrice()) < 0);
    }
}
