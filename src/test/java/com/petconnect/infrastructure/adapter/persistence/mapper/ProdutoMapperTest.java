package com.petconnect.infrastructure.adapter.persistence.mapper;

import com.petconnect.domain.produto.entity.Produto;
import com.petconnect.infrastructure.adapter.persistence.entity.ProdutoJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProdutoMapperTest {

    private ProdutoMapper produtoMapper;
    private UUID lojistaId;
    private UUID produtoId;

    @BeforeEach
    void setUp() {
        produtoMapper = new ProdutoMapper();
        lojistaId = UUID.randomUUID();
        produtoId = UUID.randomUUID();
    }

    @Test
    void shouldMapProdutoToJpaEntitySuccessfully() {

        LocalDateTime now = LocalDateTime.now();
        Produto produto = Produto.builder()
                .id(produtoId)
                .lojistaId(lojistaId)
                .nome("Ração Premium")
                .description("Ração premium para cães adultos")
                .price(new BigDecimal("29.99"))
                .photoUrl("https://example.com/photo.jpg")
                .unitOfMeasure("kg")
                .createdAt(now)
                .updatedAt(now)
                .build();


        ProdutoJpaEntity result = produtoMapper.toJpaEntity(produto);


        assertNotNull(result);
        assertEquals(produtoId, result.getId());
        assertEquals(lojistaId, result.getLojistaId());
        assertEquals("Ração Premium", result.getNome());
        assertEquals("Ração premium para cães adultos", result.getDescription());
        assertEquals(new BigDecimal("29.99"), result.getPrice());
        assertEquals("https://example.com/photo.jpg", result.getPhotoUrl());
        assertEquals("kg", result.getUnitOfMeasure());
    }

    @Test
    void shouldMapProdutoToJpaEntityWithoutId() {

        Produto produto = Produto.builder()
                .lojistaId(lojistaId)
                .nome("Produto Novo")
                .price(new BigDecimal("15.50"))
                .unitOfMeasure("un")
                .build();


        ProdutoJpaEntity result = produtoMapper.toJpaEntity(produto);


        assertNotNull(result);
        assertNull(result.getId()); // ID should not be set
        assertEquals(lojistaId, result.getLojistaId());
        assertEquals("Produto Novo", result.getNome());
        assertEquals(new BigDecimal("15.50"), result.getPrice());
        assertEquals("un", result.getUnitOfMeasure());
    }

    @Test
    void shouldMapJpaEntityToProdutoSuccessfully() {

        LocalDateTime now = LocalDateTime.now();
        ProdutoJpaEntity jpaEntity = ProdutoJpaEntity.builder()
                .lojistaId(lojistaId)
                .nome("Ração Premium")
                .description("Ração premium para cães adultos")
                .price(new BigDecimal("29.99"))
                .photoUrl("https://example.com/photo.jpg")
                .unitOfMeasure("kg")
                .build();
        jpaEntity.setId(produtoId);
        jpaEntity.setCreatedAt(now);
        jpaEntity.setUpdatedAt(now);


        Produto result = produtoMapper.toDomainEntity(jpaEntity);


        assertNotNull(result);
        assertEquals(produtoId, result.getId());
        assertEquals(lojistaId, result.getLojistaId());
        assertEquals("Ração Premium", result.getNome());
        assertEquals("Ração premium para cães adultos", result.getDescription());
        assertEquals(new BigDecimal("29.99"), result.getPrice());
        assertEquals("https://example.com/photo.jpg", result.getPhotoUrl());
        assertEquals("kg", result.getUnitOfMeasure());
        assertEquals(now, result.getCreatedAt());
        assertEquals(now, result.getUpdatedAt());
    }

    @Test
    void shouldReturnNullWhenMappingNullProdutoToJpaEntity() {

        ProdutoJpaEntity result = produtoMapper.toJpaEntity(null);


        assertNull(result);
    }

    @Test
    void shouldReturnNullWhenMappingNullJpaEntityToProduto() {

        Produto result = produtoMapper.toDomainEntity(null);


        assertNull(result);
    }

    @Test
    void shouldMapProdutoWithNullOptionalFields() {

        Produto produto = Produto.builder()
                .id(produtoId)
                .lojistaId(lojistaId)
                .nome("Produto Simples")
                .price(new BigDecimal("10.00"))
                .unitOfMeasure("un")
                .description(null)
                .photoUrl(null)
                .build();


        ProdutoJpaEntity result = produtoMapper.toJpaEntity(produto);


        assertNotNull(result);
        assertEquals(produtoId, result.getId());
        assertEquals("Produto Simples", result.getNome());
        assertNull(result.getDescription());
        assertNull(result.getPhotoUrl());
        assertEquals(new BigDecimal("10.00"), result.getPrice());
        assertEquals("un", result.getUnitOfMeasure());
    }

    @Test
    void shouldMapJpaEntityWithNullOptionalFields() {

        ProdutoJpaEntity jpaEntity = ProdutoJpaEntity.builder()
                .lojistaId(lojistaId)
                .nome("Produto Simples")
                .price(new BigDecimal("10.00"))
                .unitOfMeasure("un")
                .description(null)
                .photoUrl(null)
                .build();
        jpaEntity.setId(produtoId);


        Produto result = produtoMapper.toDomainEntity(jpaEntity);


        assertNotNull(result);
        assertEquals(produtoId, result.getId());
        assertEquals("Produto Simples", result.getNome());
        assertNull(result.getDescription());
        assertNull(result.getPhotoUrl());
        assertEquals(new BigDecimal("10.00"), result.getPrice());
        assertEquals("un", result.getUnitOfMeasure());
    }

    @Test
    void shouldPreservePrecisionInPriceMapping() {

        BigDecimal precisePrice = new BigDecimal("123.456789");
        Produto produto = Produto.builder()
                .lojistaId(lojistaId)
                .nome("Produto Preciso")
                .price(precisePrice)
                .unitOfMeasure("kg")
                .build();


        ProdutoJpaEntity jpaEntity = produtoMapper.toJpaEntity(produto);
        Produto mappedBack = produtoMapper.toDomainEntity(jpaEntity);


        assertNotNull(jpaEntity);
        assertNotNull(mappedBack);
        assertEquals(precisePrice, jpaEntity.getPrice());
        assertEquals(precisePrice, mappedBack.getPrice());
    }

    @Test
    void shouldHandleSpecialCharactersInFields() {

        Produto produto = Produto.builder()
                .lojistaId(lojistaId)
                .nome("Produto Especial & Único ™")
                .description("Descrição com acentos: ção, ã, é, ñ")
                .price(new BigDecimal("99.99"))
                .unitOfMeasure("kg")
                .photoUrl("https://example.com/foto_ração_cães.jpg")
                .build();


        ProdutoJpaEntity jpaEntity = produtoMapper.toJpaEntity(produto);
        Produto mappedBack = produtoMapper.toDomainEntity(jpaEntity);


        assertNotNull(jpaEntity);
        assertNotNull(mappedBack);
        assertEquals("Produto Especial & Único ™", jpaEntity.getNome());
        assertEquals("Descrição com acentos: ção, ã, é, ñ", jpaEntity.getDescription());
        assertEquals("https://example.com/foto_ração_cães.jpg", jpaEntity.getPhotoUrl());
        assertEquals(produto.getNome(), mappedBack.getNome());
        assertEquals(produto.getDescription(), mappedBack.getDescription());
        assertEquals(produto.getPhotoUrl(), mappedBack.getPhotoUrl());
    }

    @Test
    void shouldHandleEmptyStringFields() {

        Produto produto = Produto.builder()
                .lojistaId(lojistaId)
                .nome("Produto com Campos Vazios")
                .description("")
                .photoUrl("")
                .price(new BigDecimal("5.00"))
                .unitOfMeasure("un")
                .build();


        ProdutoJpaEntity jpaEntity = produtoMapper.toJpaEntity(produto);
        Produto mappedBack = produtoMapper.toDomainEntity(jpaEntity);


        assertNotNull(jpaEntity);
        assertNotNull(mappedBack);
        assertEquals("", jpaEntity.getDescription());
        assertEquals("", jpaEntity.getPhotoUrl());
        assertEquals("", mappedBack.getDescription());
        assertEquals("", mappedBack.getPhotoUrl());
    }

    @Test
    void shouldMaintainBidirectionalMappingConsistency() {

        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedAt = LocalDateTime.now();
        
        Produto originalProduto = Produto.builder()
                .id(produtoId)
                .lojistaId(lojistaId)
                .nome("Produto Teste Consistência")
                .description("Teste de mapeamento bidirecional")
                .price(new BigDecimal("45.67"))
                .photoUrl("https://test.com/image.png")
                .unitOfMeasure("litros")
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();


        ProdutoJpaEntity jpaEntity = produtoMapper.toJpaEntity(originalProduto);
        jpaEntity.setCreatedAt(createdAt);
        jpaEntity.setUpdatedAt(updatedAt);
        Produto mappedBackProduto = produtoMapper.toDomainEntity(jpaEntity);


        assertNotNull(jpaEntity);
        assertNotNull(mappedBackProduto);
        
        assertEquals(originalProduto.getId(), mappedBackProduto.getId());
        assertEquals(originalProduto.getLojistaId(), mappedBackProduto.getLojistaId());
        assertEquals(originalProduto.getNome(), mappedBackProduto.getNome());
        assertEquals(originalProduto.getDescription(), mappedBackProduto.getDescription());
        assertEquals(originalProduto.getPrice(), mappedBackProduto.getPrice());
        assertEquals(originalProduto.getPhotoUrl(), mappedBackProduto.getPhotoUrl());
        assertEquals(originalProduto.getUnitOfMeasure(), mappedBackProduto.getUnitOfMeasure());
        assertEquals(originalProduto.getCreatedAt(), mappedBackProduto.getCreatedAt());
        assertEquals(originalProduto.getUpdatedAt(), mappedBackProduto.getUpdatedAt());
    }

    @Test
    void shouldHandleLargeDecimalValues() {

        BigDecimal largePrice = new BigDecimal("999999.99");
        Produto produto = Produto.builder()
                .lojistaId(lojistaId)
                .nome("Produto Caro")
                .price(largePrice)
                .unitOfMeasure("un")
                .build();


        ProdutoJpaEntity jpaEntity = produtoMapper.toJpaEntity(produto);
        Produto mappedBack = produtoMapper.toDomainEntity(jpaEntity);


        assertNotNull(jpaEntity);
        assertNotNull(mappedBack);
        assertEquals(largePrice, jpaEntity.getPrice());
        assertEquals(largePrice, mappedBack.getPrice());
    }

    @Test
    void shouldHandleSmallDecimalValues() {

        BigDecimal smallPrice = new BigDecimal("0.01");
        Produto produto = Produto.builder()
                .lojistaId(lojistaId)
                .nome("Produto Barato")
                .price(smallPrice)
                .unitOfMeasure("mg")
                .build();


        ProdutoJpaEntity jpaEntity = produtoMapper.toJpaEntity(produto);
        Produto mappedBack = produtoMapper.toDomainEntity(jpaEntity);


        assertNotNull(jpaEntity);
        assertNotNull(mappedBack);
        assertEquals(smallPrice, jpaEntity.getPrice());
        assertEquals(smallPrice, mappedBack.getPrice());
    }

    @Test
    void shouldHandleVeryLongStrings() {

        String longName = "Produto com Nome Muito Longo ".repeat(10);
        String longDescription = "Esta é uma descrição muito longa para testar os limites do mapeamento. ".repeat(20);
        String longUrl = "https://example.com/" + "very-long-path/".repeat(50) + "image.jpg";
        
        Produto produto = Produto.builder()
                .lojistaId(lojistaId)
                .nome(longName)
                .description(longDescription)
                .photoUrl(longUrl)
                .price(new BigDecimal("19.99"))
                .unitOfMeasure("un")
                .build();


        ProdutoJpaEntity jpaEntity = produtoMapper.toJpaEntity(produto);
        Produto mappedBack = produtoMapper.toDomainEntity(jpaEntity);


        assertNotNull(jpaEntity);
        assertNotNull(mappedBack);
        assertEquals(longName, jpaEntity.getNome());
        assertEquals(longDescription, jpaEntity.getDescription());
        assertEquals(longUrl, jpaEntity.getPhotoUrl());
        assertEquals(produto.getNome(), mappedBack.getNome());
        assertEquals(produto.getDescription(), mappedBack.getDescription());
        assertEquals(produto.getPhotoUrl(), mappedBack.getPhotoUrl());
    }
}
