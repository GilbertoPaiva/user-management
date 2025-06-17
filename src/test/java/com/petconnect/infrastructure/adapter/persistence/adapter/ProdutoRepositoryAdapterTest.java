package com.petconnect.infrastructure.adapter.persistence.adapter;

import com.petconnect.domain.produto.entity.Produto;
import com.petconnect.infrastructure.adapter.persistence.entity.ProdutoJpaEntity;
import com.petconnect.infrastructure.adapter.persistence.mapper.ProdutoMapper;
import com.petconnect.infrastructure.adapter.persistence.repository.ProdutoJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProdutoRepositoryAdapterTest {

    @Mock
    private ProdutoJpaRepository produtoJpaRepository;

    @Mock
    private ProdutoMapper produtoMapper;

    @InjectMocks
    private ProdutoRepositoryAdapter produtoRepositoryAdapter;

    private Produto produto;
    private ProdutoJpaEntity produtoJpaEntity;
    private UUID lojistaId;
    private UUID produtoId;

    @BeforeEach
    void setUp() {
        lojistaId = UUID.randomUUID();
        produtoId = UUID.randomUUID();
        
        produto = Produto.builder()
                .id(produtoId)
                .lojistaId(lojistaId)
                .nome("Ração Premium")
                .description("Ração premium para cães")
                .price(new BigDecimal("29.99"))
                .photoUrl("https://example.com/photo.jpg")
                .unitOfMeasure("kg")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        produtoJpaEntity = ProdutoJpaEntity.builder()
                .lojistaId(lojistaId)
                .nome("Ração Premium")
                .description("Ração premium para cães")
                .price(new BigDecimal("29.99"))
                .photoUrl("https://example.com/photo.jpg")
                .unitOfMeasure("kg")
                .build();
        produtoJpaEntity.setId(produtoId);
    }

    @Test
    void shouldSaveProdutoSuccessfully() {
        // Given
        when(produtoMapper.toJpaEntity(produto)).thenReturn(produtoJpaEntity);
        when(produtoJpaRepository.save(produtoJpaEntity)).thenReturn(produtoJpaEntity);
        when(produtoMapper.toDomainEntity(produtoJpaEntity)).thenReturn(produto);

        // When
        Produto result = produtoRepositoryAdapter.save(produto);

        // Then
        assertNotNull(result);
        assertEquals(produto.getId(), result.getId());
        assertEquals(produto.getNome(), result.getNome());
        
        verify(produtoMapper, times(1)).toJpaEntity(produto);
        verify(produtoJpaRepository, times(1)).save(produtoJpaEntity);
        verify(produtoMapper, times(1)).toDomainEntity(produtoJpaEntity);
    }

    @Test
    void shouldFindProdutoByIdSuccessfully() {
        // Given
        when(produtoJpaRepository.findById(produtoId)).thenReturn(Optional.of(produtoJpaEntity));
        when(produtoMapper.toDomainEntity(produtoJpaEntity)).thenReturn(produto);

        // When
        Optional<Produto> result = produtoRepositoryAdapter.findById(produtoId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(produto.getId(), result.get().getId());
        assertEquals(produto.getNome(), result.get().getNome());
        
        verify(produtoJpaRepository, times(1)).findById(produtoId);
        verify(produtoMapper, times(1)).toDomainEntity(produtoJpaEntity);
    }

    @Test
    void shouldReturnEmptyWhenProdutoNotFound() {
        // Given
        when(produtoJpaRepository.findById(produtoId)).thenReturn(Optional.empty());

        // When
        Optional<Produto> result = produtoRepositoryAdapter.findById(produtoId);

        // Then
        assertFalse(result.isPresent());
        
        verify(produtoJpaRepository, times(1)).findById(produtoId);
        verify(produtoMapper, never()).toDomainEntity(any());
    }

    @Test
    void shouldFindProdutosByLojistaId() {
        // Given
        List<UUID> produtoIds = Arrays.asList(produtoId, UUID.randomUUID());
        List<ProdutoJpaEntity> jpaEntities = Arrays.asList(produtoJpaEntity);
        List<Produto> produtos = Arrays.asList(produto);

        when(produtoJpaRepository.findIdsByLojistaId(lojistaId)).thenReturn(produtoIds);
        when(produtoJpaRepository.findById(produtoId)).thenReturn(Optional.of(produtoJpaEntity));
        when(produtoJpaRepository.findById(produtoIds.get(1))).thenReturn(Optional.empty());
        when(produtoMapper.toDomainEntity(produtoJpaEntity)).thenReturn(produto);

        // When
        List<Produto> result = produtoRepositoryAdapter.findByLojistaId(lojistaId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(produto.getId(), result.get(0).getId());
        
        verify(produtoJpaRepository, times(1)).findIdsByLojistaId(lojistaId);
        verify(produtoMapper, times(1)).toDomainEntity(produtoJpaEntity);
    }

    @Test
    void shouldFindAllProdutosWithPagination() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProdutoJpaEntity> jpaPage = new PageImpl<>(Arrays.asList(produtoJpaEntity));
        
        when(produtoJpaRepository.findAll(pageable)).thenReturn(jpaPage);
        when(produtoMapper.toDomainEntity(produtoJpaEntity)).thenReturn(produto);

        // When
        Page<Produto> result = produtoRepositoryAdapter.findAll(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(produto.getId(), result.getContent().get(0).getId());
        
        verify(produtoJpaRepository, times(1)).findAll(pageable);
        verify(produtoMapper, times(1)).toDomainEntity(produtoJpaEntity);
    }

    @Test
    void shouldFindProdutosByLojistaIdWithPagination() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProdutoJpaEntity> jpaPage = new PageImpl<>(Arrays.asList(produtoJpaEntity));
        
        when(produtoJpaRepository.findByLojistaId(lojistaId, pageable)).thenReturn(jpaPage);
        when(produtoMapper.toDomainEntity(produtoJpaEntity)).thenReturn(produto);

        // When
        Page<Produto> result = produtoRepositoryAdapter.findByLojistaId(lojistaId, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(produto.getId(), result.getContent().get(0).getId());
        
        verify(produtoJpaRepository, times(1)).findByLojistaId(lojistaId, pageable);
        verify(produtoMapper, times(1)).toDomainEntity(produtoJpaEntity);
    }

    @Test
    void shouldDeleteProdutoById() {
        // When
        produtoRepositoryAdapter.deleteById(produtoId);

        // Then
        verify(produtoJpaRepository, times(1)).deleteById(produtoId);
    }

    @Test
    void shouldFindProdutosByNomeContaining() {
        // Given
        String nome = "Ração";
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProdutoJpaEntity> jpaPage = new PageImpl<>(Arrays.asList(produtoJpaEntity));
        
        when(produtoJpaRepository.findByNomeContainingIgnoreCase(nome, pageable)).thenReturn(jpaPage);
        when(produtoMapper.toDomainEntity(produtoJpaEntity)).thenReturn(produto);

        // When
        Page<Produto> result = produtoRepositoryAdapter.findByNomeContainingIgnoreCase(nome, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(produto.getId(), result.getContent().get(0).getId());
        
        verify(produtoJpaRepository, times(1)).findByNomeContainingIgnoreCase(nome, pageable);
        verify(produtoMapper, times(1)).toDomainEntity(produtoJpaEntity);
    }

    @Test
    void shouldFindProdutosByPriceBetween() {
        // Given
        BigDecimal minPrice = new BigDecimal("20.00");
        BigDecimal maxPrice = new BigDecimal("40.00");
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProdutoJpaEntity> jpaPage = new PageImpl<>(Arrays.asList(produtoJpaEntity));
        
        when(produtoJpaRepository.findByPriceBetween(minPrice, maxPrice, pageable)).thenReturn(jpaPage);
        when(produtoMapper.toDomainEntity(produtoJpaEntity)).thenReturn(produto);

        // When
        Page<Produto> result = produtoRepositoryAdapter.findByPriceBetween(minPrice, maxPrice, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(produto.getId(), result.getContent().get(0).getId());
        
        verify(produtoJpaRepository, times(1)).findByPriceBetween(minPrice, maxPrice, pageable);
        verify(produtoMapper, times(1)).toDomainEntity(produtoJpaEntity);
    }

    @Test
    void shouldFindProdutosByNomeOrDescriptionContaining() {
        // Given
        String searchTerm = "premium";
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProdutoJpaEntity> jpaPage = new PageImpl<>(Arrays.asList(produtoJpaEntity));
        
        when(produtoJpaRepository.findByNomeContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                searchTerm, searchTerm, pageable)).thenReturn(jpaPage);
        when(produtoMapper.toDomainEntity(produtoJpaEntity)).thenReturn(produto);

        // When
        Page<Produto> result = produtoRepositoryAdapter.findByNomeContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                searchTerm, searchTerm, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(produto.getId(), result.getContent().get(0).getId());
        
        verify(produtoJpaRepository, times(1)).findByNomeContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                searchTerm, searchTerm, pageable);
        verify(produtoMapper, times(1)).toDomainEntity(produtoJpaEntity);
    }

    @Test
    void shouldHandleEmptyListFromFindByLojistaId() {
        // Given
        when(produtoJpaRepository.findIdsByLojistaId(lojistaId)).thenReturn(Arrays.asList());

        // When
        List<Produto> result = produtoRepositoryAdapter.findByLojistaId(lojistaId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        verify(produtoJpaRepository, times(1)).findIdsByLojistaId(lojistaId);
        verify(produtoMapper, never()).toDomainEntity(any());
    }

    @Test
    void shouldHandleNullReturnFromMapper() {
        // Given
        when(produtoJpaRepository.findById(produtoId)).thenReturn(Optional.of(produtoJpaEntity));
        when(produtoMapper.toDomainEntity(produtoJpaEntity)).thenReturn(null);

        // When
        Optional<Produto> result = produtoRepositoryAdapter.findById(produtoId);

        // Then
        assertTrue(result.isPresent());
        assertNull(result.get());
        
        verify(produtoJpaRepository, times(1)).findById(produtoId);
        verify(produtoMapper, times(1)).toDomainEntity(produtoJpaEntity);
    }

    @Test
    void shouldHandleExceptionDuringFindAll() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        when(produtoJpaRepository.findAll(pageable)).thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> produtoRepositoryAdapter.findAll(pageable));
        
        assertEquals("Database error", exception.getMessage());
        verify(produtoJpaRepository, times(1)).findAll(pageable);
    }

    @Test
    void shouldHandleExceptionDuringSave() {
        // Given
        when(produtoMapper.toJpaEntity(produto)).thenReturn(produtoJpaEntity);
        when(produtoJpaRepository.save(produtoJpaEntity)).thenThrow(new RuntimeException("Save failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> produtoRepositoryAdapter.save(produto));
        
        assertEquals("Save failed", exception.getMessage());
        verify(produtoMapper, times(1)).toJpaEntity(produto);
        verify(produtoJpaRepository, times(1)).save(produtoJpaEntity);
    }

    @Test
    void shouldVerifyCorrectMethodCallsOnComplexSearch() {
        // Given
        String nome = "ração";
        String description = "premium";
        Pageable pageable = PageRequest.of(0, 5);
        Page<ProdutoJpaEntity> jpaPage = new PageImpl<>(Arrays.asList(produtoJpaEntity));
        
        when(produtoJpaRepository.findByNomeContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                nome, description, pageable)).thenReturn(jpaPage);
        when(produtoMapper.toDomainEntity(produtoJpaEntity)).thenReturn(produto);

        // When
        Page<Produto> result = produtoRepositoryAdapter.findByNomeContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                nome, description, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(5, result.getSize());
        
        verify(produtoJpaRepository, times(1)).findByNomeContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                eq(nome), eq(description), eq(pageable));
        verify(produtoMapper, times(1)).toDomainEntity(produtoJpaEntity);
    }
}
