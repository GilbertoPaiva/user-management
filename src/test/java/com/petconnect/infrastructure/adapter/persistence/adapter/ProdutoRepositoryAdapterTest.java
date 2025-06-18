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

        when(produtoMapper.toJpaEntity(produto)).thenReturn(produtoJpaEntity);
        when(produtoJpaRepository.save(produtoJpaEntity)).thenReturn(produtoJpaEntity);
        when(produtoMapper.toDomainEntity(produtoJpaEntity)).thenReturn(produto);


        Produto result = produtoRepositoryAdapter.save(produto);


        assertNotNull(result);
        assertEquals(produto.getId(), result.getId());
        assertEquals(produto.getNome(), result.getNome());
        
        verify(produtoMapper, times(1)).toJpaEntity(produto);
        verify(produtoJpaRepository, times(1)).save(produtoJpaEntity);
        verify(produtoMapper, times(1)).toDomainEntity(produtoJpaEntity);
    }

    @Test
    void shouldFindProdutoByIdSuccessfully() {

        when(produtoJpaRepository.findById(produtoId)).thenReturn(Optional.of(produtoJpaEntity));
        when(produtoMapper.toDomainEntity(produtoJpaEntity)).thenReturn(produto);


        Optional<Produto> result = produtoRepositoryAdapter.findById(produtoId);


        assertTrue(result.isPresent());
        assertEquals(produto.getId(), result.get().getId());
        assertEquals(produto.getNome(), result.get().getNome());
        
        verify(produtoJpaRepository, times(1)).findById(produtoId);
        verify(produtoMapper, times(1)).toDomainEntity(produtoJpaEntity);
    }

    @Test
    void shouldReturnEmptyWhenProdutoNotFound() {

        when(produtoJpaRepository.findById(produtoId)).thenReturn(Optional.empty());


        Optional<Produto> result = produtoRepositoryAdapter.findById(produtoId);


        assertFalse(result.isPresent());
        
        verify(produtoJpaRepository, times(1)).findById(produtoId);
        verify(produtoMapper, never()).toDomainEntity(any());
    }

    @Test
    void shouldFindProdutosByLojistaId() {

        List<UUID> produtoIds = Arrays.asList(produtoId, UUID.randomUUID());

        when(produtoJpaRepository.findIdsByLojistaId(lojistaId)).thenReturn(produtoIds);
        when(produtoJpaRepository.findById(produtoId)).thenReturn(Optional.of(produtoJpaEntity));
        when(produtoJpaRepository.findById(produtoIds.get(1))).thenReturn(Optional.empty());
        when(produtoMapper.toDomainEntity(produtoJpaEntity)).thenReturn(produto);


        List<Produto> result = produtoRepositoryAdapter.findByLojistaId(lojistaId);


        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(produto.getId(), result.get(0).getId());
        
        verify(produtoJpaRepository, times(1)).findIdsByLojistaId(lojistaId);
        verify(produtoMapper, times(1)).toDomainEntity(produtoJpaEntity);
    }

    @Test
    void shouldFindAllProdutosWithPagination() {

        Pageable pageable = PageRequest.of(0, 10);
        Page<ProdutoJpaEntity> jpaPage = new PageImpl<>(Arrays.asList(produtoJpaEntity));
        
        when(produtoJpaRepository.findAll(pageable)).thenReturn(jpaPage);
        when(produtoMapper.toDomainEntity(produtoJpaEntity)).thenReturn(produto);


        Page<Produto> result = produtoRepositoryAdapter.findAll(pageable);


        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(produto.getId(), result.getContent().get(0).getId());
        
        verify(produtoJpaRepository, times(1)).findAll(pageable);
        verify(produtoMapper, times(1)).toDomainEntity(produtoJpaEntity);
    }

    @Test
    void shouldFindProdutosByLojistaIdWithPagination() {

        Pageable pageable = PageRequest.of(0, 10);
        Page<ProdutoJpaEntity> jpaPage = new PageImpl<>(Arrays.asList(produtoJpaEntity));
        
        when(produtoJpaRepository.findByLojistaId(lojistaId, pageable)).thenReturn(jpaPage);
        when(produtoMapper.toDomainEntity(produtoJpaEntity)).thenReturn(produto);


        Page<Produto> result = produtoRepositoryAdapter.findByLojistaId(lojistaId, pageable);


        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(produto.getId(), result.getContent().get(0).getId());
        
        verify(produtoJpaRepository, times(1)).findByLojistaId(lojistaId, pageable);
        verify(produtoMapper, times(1)).toDomainEntity(produtoJpaEntity);
    }

    @Test
    void shouldDeleteProdutoById() {

        produtoRepositoryAdapter.deleteById(produtoId);


        verify(produtoJpaRepository, times(1)).deleteById(produtoId);
    }

    @Test
    void shouldFindProdutosByNomeContaining() {

        String nome = "Ração";
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProdutoJpaEntity> jpaPage = new PageImpl<>(Arrays.asList(produtoJpaEntity));
        
        when(produtoJpaRepository.findByNomeContainingIgnoreCase(nome, pageable)).thenReturn(jpaPage);
        when(produtoMapper.toDomainEntity(produtoJpaEntity)).thenReturn(produto);


        Page<Produto> result = produtoRepositoryAdapter.findByNomeContainingIgnoreCase(nome, pageable);


        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(produto.getId(), result.getContent().get(0).getId());
        
        verify(produtoJpaRepository, times(1)).findByNomeContainingIgnoreCase(nome, pageable);
        verify(produtoMapper, times(1)).toDomainEntity(produtoJpaEntity);
    }

    @Test
    void shouldFindProdutosByPriceBetween() {

        BigDecimal minPrice = new BigDecimal("20.00");
        BigDecimal maxPrice = new BigDecimal("40.00");
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProdutoJpaEntity> jpaPage = new PageImpl<>(Arrays.asList(produtoJpaEntity));
        
        when(produtoJpaRepository.findByPriceBetween(minPrice, maxPrice, pageable)).thenReturn(jpaPage);
        when(produtoMapper.toDomainEntity(produtoJpaEntity)).thenReturn(produto);


        Page<Produto> result = produtoRepositoryAdapter.findByPriceBetween(minPrice, maxPrice, pageable);


        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(produto.getId(), result.getContent().get(0).getId());
        
        verify(produtoJpaRepository, times(1)).findByPriceBetween(minPrice, maxPrice, pageable);
        verify(produtoMapper, times(1)).toDomainEntity(produtoJpaEntity);
    }

    @Test
    void shouldFindProdutosByNomeOrDescriptionContaining() {

        String searchTerm = "premium";
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProdutoJpaEntity> jpaPage = new PageImpl<>(Arrays.asList(produtoJpaEntity));
        
        when(produtoJpaRepository.findByNomeOrDescriptionContaining(
                searchTerm, pageable)).thenReturn(jpaPage);
        when(produtoMapper.toDomainEntity(produtoJpaEntity)).thenReturn(produto);


        Page<Produto> result = produtoRepositoryAdapter.findByNomeContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                searchTerm, searchTerm, pageable);


        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(produto.getId(), result.getContent().get(0).getId());
        
        verify(produtoJpaRepository, times(1)).findByNomeOrDescriptionContaining(
                searchTerm, pageable);
        verify(produtoMapper, times(1)).toDomainEntity(produtoJpaEntity);
    }

    @Test
    void shouldHandleEmptyListFromFindByLojistaId() {

        when(produtoJpaRepository.findIdsByLojistaId(lojistaId)).thenReturn(Arrays.asList());


        List<Produto> result = produtoRepositoryAdapter.findByLojistaId(lojistaId);


        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        verify(produtoJpaRepository, times(1)).findIdsByLojistaId(lojistaId);
        verify(produtoMapper, never()).toDomainEntity(any());
    }

    @Test
    void shouldHandleNullReturnFromMapper() {

        when(produtoJpaRepository.findById(produtoId)).thenReturn(Optional.of(produtoJpaEntity));
        when(produtoMapper.toDomainEntity(produtoJpaEntity)).thenReturn(null);


        Optional<Produto> result = produtoRepositoryAdapter.findById(produtoId);


        assertFalse(result.isPresent());
        
        verify(produtoJpaRepository, times(1)).findById(produtoId);
        verify(produtoMapper, times(1)).toDomainEntity(produtoJpaEntity);
    }

    @Test
    void shouldHandleExceptionDuringFindAll() {

        Pageable pageable = PageRequest.of(0, 10);
        when(produtoJpaRepository.findAll(pageable)).thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> produtoRepositoryAdapter.findAll(pageable));
        
        assertEquals("Database error", exception.getMessage());
        verify(produtoJpaRepository, times(1)).findAll(pageable);
    }

    @Test
    void shouldHandleExceptionDuringSave() {

        when(produtoMapper.toJpaEntity(produto)).thenReturn(produtoJpaEntity);
        when(produtoJpaRepository.save(produtoJpaEntity)).thenThrow(new RuntimeException("Save failed"));

        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> produtoRepositoryAdapter.save(produto));
        
        assertEquals("Save failed", exception.getMessage());
        verify(produtoMapper, times(1)).toJpaEntity(produto);
        verify(produtoJpaRepository, times(1)).save(produtoJpaEntity);
    }

    @Test
    void shouldVerifyCorrectMethodCallsOnComplexSearch() {

        String nome = "ração";
        String description = "premium";
        Pageable pageable = PageRequest.of(0, 5);
        Page<ProdutoJpaEntity> jpaPage = new PageImpl<>(Arrays.asList(produtoJpaEntity), pageable, 1);
        
        when(produtoJpaRepository.findByNomeOrDescriptionContaining(
                nome, pageable)).thenReturn(jpaPage);
        when(produtoMapper.toDomainEntity(produtoJpaEntity)).thenReturn(produto);

        Page<Produto> result = produtoRepositoryAdapter.findByNomeContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                nome, description, pageable);


        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(5, result.getSize());
        
        verify(produtoJpaRepository, times(1)).findByNomeOrDescriptionContaining(
                eq(nome), eq(pageable));
        verify(produtoMapper, times(1)).toDomainEntity(produtoJpaEntity);
    }
}
