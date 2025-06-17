package com.petconnect.domain.produto.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

class ProdutoTest {

    @Test
    void shouldCreateProdutoWithRequiredFields() {
        // Given
        UUID id = UUID.randomUUID();
        UUID lojistaId = UUID.randomUUID();
        String nome = "Ração Premium";
        BigDecimal price = new BigDecimal("29.99");
        
        // When
        Produto produto = Produto.builder()
                .id(id)
                .lojistaId(lojistaId)
                .nome(nome)
                .description("Ração premium para cães")
                .price(price)
                .unitOfMeasure("kg")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        // Then
        assertNotNull(produto);
        assertEquals(id, produto.getId());
        assertEquals(lojistaId, produto.getLojistaId());
        assertEquals(nome, produto.getNome());
        assertEquals(price, produto.getPrice());
        assertEquals("kg", produto.getUnitOfMeasure());
    }

    @Test
    void shouldUpdateProdutoInfo() {
        // Given
        Produto produto = Produto.builder()
                .nome("Nome Antigo")
                .description("Descrição Antiga")
                .price(new BigDecimal("10.00"))
                .unitOfMeasure("un")
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();
        
        String novoNome = "Nome Novo";
        String novaDescricao = "Nova Descrição";
        BigDecimal novoPreco = new BigDecimal("15.99");
        String novaUnidade = "kg";
        LocalDateTime timeBeforeUpdate = LocalDateTime.now();
        
        // When
        produto.updateInfo(novoNome, novaDescricao, novoPreco, novaUnidade);
        
        // Then
        assertEquals(novoNome, produto.getNome());
        assertEquals(novaDescricao, produto.getDescription());
        assertEquals(novoPreco, produto.getPrice());
        assertEquals(novaUnidade, produto.getUnitOfMeasure());
        assertTrue(produto.getUpdatedAt().isAfter(timeBeforeUpdate));
    }

    @Test
    void shouldValidatePrice() {
        // Given
        Produto produtoValidPrice = Produto.builder()
                .price(new BigDecimal("10.50"))
                .build();
        
        Produto produtoInvalidPrice = Produto.builder()
                .price(BigDecimal.ZERO)
                .build();
        
        Produto produtoNullPrice = Produto.builder()
                .price(null)
                .build();
        
        // When & Then
        assertTrue(produtoValidPrice.isValidPrice());
        assertFalse(produtoInvalidPrice.isValidPrice());
        assertFalse(produtoNullPrice.isValidPrice());
    }
}
