package com.petconnect.infrastructure.adapter.persistence.adapter;

import com.petconnect.domain.servico.entity.Servico;
import com.petconnect.infrastructure.adapter.persistence.entity.ServicoJpaEntity;
import com.petconnect.infrastructure.adapter.persistence.mapper.ServicoMapper;
import com.petconnect.infrastructure.adapter.persistence.repository.ServicoJpaRepository;
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
class ServicoRepositoryAdapterTest {

    @Mock
    private ServicoJpaRepository servicoJpaRepository;

    @Mock
    private ServicoMapper servicoMapper;

    @InjectMocks
    private ServicoRepositoryAdapter servicoRepositoryAdapter;

    private Servico servico;
    private ServicoJpaEntity servicoJpaEntity;
    private UUID veterinarioId;
    private UUID servicoId;

    @BeforeEach
    void setUp() {
        veterinarioId = UUID.randomUUID();
        servicoId = UUID.randomUUID();
        
        servico = Servico.builder()
                .id(servicoId)
                .veterinarioId(veterinarioId)
                .nome("Consulta Veterinária")
                .description("Consulta clínica geral")
                .price(new BigDecimal("80.00"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        servicoJpaEntity = ServicoJpaEntity.builder()
                .veterinarioId(veterinarioId)
                .nome("Consulta Veterinária")
                .description("Consulta clínica geral")
                .price(new BigDecimal("80.00"))
                .build();
        servicoJpaEntity.setId(servicoId);
    }

    @Test
    void shouldSaveServicoSuccessfully() {
        // Given
        when(servicoMapper.toJpaEntity(servico)).thenReturn(servicoJpaEntity);
        when(servicoJpaRepository.save(servicoJpaEntity)).thenReturn(servicoJpaEntity);
        when(servicoMapper.toDomainEntity(servicoJpaEntity)).thenReturn(servico);

        // When
        Servico result = servicoRepositoryAdapter.save(servico);

        // Then
        assertNotNull(result);
        assertEquals(servico.getId(), result.getId());
        assertEquals(servico.getNome(), result.getNome());
        
        verify(servicoMapper, times(1)).toJpaEntity(servico);
        verify(servicoJpaRepository, times(1)).save(servicoJpaEntity);
        verify(servicoMapper, times(1)).toDomainEntity(servicoJpaEntity);
    }

    @Test
    void shouldFindServicoByIdSuccessfully() {
        // Given
        when(servicoJpaRepository.findById(servicoId)).thenReturn(Optional.of(servicoJpaEntity));
        when(servicoMapper.toDomainEntity(servicoJpaEntity)).thenReturn(servico);

        // When
        Optional<Servico> result = servicoRepositoryAdapter.findById(servicoId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(servico.getId(), result.get().getId());
        assertEquals(servico.getNome(), result.get().getNome());
        
        verify(servicoJpaRepository, times(1)).findById(servicoId);
        verify(servicoMapper, times(1)).toDomainEntity(servicoJpaEntity);
    }

    @Test
    void shouldReturnEmptyWhenServicoNotFound() {
        // Given
        when(servicoJpaRepository.findById(servicoId)).thenReturn(Optional.empty());

        // When
        Optional<Servico> result = servicoRepositoryAdapter.findById(servicoId);

        // Then
        assertFalse(result.isPresent());
        
        verify(servicoJpaRepository, times(1)).findById(servicoId);
        verify(servicoMapper, never()).toDomainEntity(any());
    }

    @Test
    void shouldFindServicosByVeterinarioId() {
        // Given
        List<UUID> servicoIds = Arrays.asList(servicoId, UUID.randomUUID());
        List<ServicoJpaEntity> jpaEntities = Arrays.asList(servicoJpaEntity);
        List<Servico> servicos = Arrays.asList(servico);

        when(servicoJpaRepository.findIdsByVeterinarioId(veterinarioId)).thenReturn(servicoIds);
        when(servicoJpaRepository.findById(servicoId)).thenReturn(Optional.of(servicoJpaEntity));
        when(servicoJpaRepository.findById(servicoIds.get(1))).thenReturn(Optional.empty());
        when(servicoMapper.toDomainEntity(servicoJpaEntity)).thenReturn(servico);

        // When
        List<Servico> result = servicoRepositoryAdapter.findByVeterinarioId(veterinarioId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(servico.getId(), result.get(0).getId());
        
        verify(servicoJpaRepository, times(1)).findIdsByVeterinarioId(veterinarioId);
        verify(servicoMapper, times(1)).toDomainEntity(servicoJpaEntity);
    }

    @Test
    void shouldFindAllServicosWithPagination() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<ServicoJpaEntity> jpaPage = new PageImpl<>(Arrays.asList(servicoJpaEntity));
        
        when(servicoJpaRepository.findAll(pageable)).thenReturn(jpaPage);
        when(servicoMapper.toDomainEntity(servicoJpaEntity)).thenReturn(servico);

        // When
        Page<Servico> result = servicoRepositoryAdapter.findAll(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(servico.getId(), result.getContent().get(0).getId());
        
        verify(servicoJpaRepository, times(1)).findAll(pageable);
        verify(servicoMapper, times(1)).toDomainEntity(servicoJpaEntity);
    }

    @Test
    void shouldFindServicosByVeterinarioIdWithPagination() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<ServicoJpaEntity> jpaPage = new PageImpl<>(Arrays.asList(servicoJpaEntity));
        
        when(servicoJpaRepository.findByVeterinarioId(veterinarioId, pageable)).thenReturn(jpaPage);
        when(servicoMapper.toDomainEntity(servicoJpaEntity)).thenReturn(servico);

        // When
        Page<Servico> result = servicoRepositoryAdapter.findByVeterinarioId(veterinarioId, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(servico.getId(), result.getContent().get(0).getId());
        
        verify(servicoJpaRepository, times(1)).findByVeterinarioId(veterinarioId, pageable);
        verify(servicoMapper, times(1)).toDomainEntity(servicoJpaEntity);
    }

    @Test
    void shouldDeleteServicoById() {
        // When
        servicoRepositoryAdapter.deleteById(servicoId);

        // Then
        verify(servicoJpaRepository, times(1)).deleteById(servicoId);
    }

    @Test
    void shouldFindServicosByNomeContaining() {
        // Given
        String nome = "Consulta";
        Pageable pageable = PageRequest.of(0, 10);
        Page<ServicoJpaEntity> jpaPage = new PageImpl<>(Arrays.asList(servicoJpaEntity));
        
        when(servicoJpaRepository.findByNomeContainingIgnoreCase(nome, pageable)).thenReturn(jpaPage);
        when(servicoMapper.toDomainEntity(servicoJpaEntity)).thenReturn(servico);

        // When
        Page<Servico> result = servicoRepositoryAdapter.findByNomeContainingIgnoreCase(nome, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(servico.getId(), result.getContent().get(0).getId());
        
        verify(servicoJpaRepository, times(1)).findByNomeContainingIgnoreCase(nome, pageable);
        verify(servicoMapper, times(1)).toDomainEntity(servicoJpaEntity);
    }

    @Test
    void shouldFindServicosByPriceBetween() {
        // Given
        BigDecimal minPrice = new BigDecimal("50.00");
        BigDecimal maxPrice = new BigDecimal("100.00");
        Pageable pageable = PageRequest.of(0, 10);
        Page<ServicoJpaEntity> jpaPage = new PageImpl<>(Arrays.asList(servicoJpaEntity));
        
        when(servicoJpaRepository.findByPriceBetween(minPrice, maxPrice, pageable)).thenReturn(jpaPage);
        when(servicoMapper.toDomainEntity(servicoJpaEntity)).thenReturn(servico);

        // When
        Page<Servico> result = servicoRepositoryAdapter.findByPriceBetween(minPrice, maxPrice, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(servico.getId(), result.getContent().get(0).getId());
        
        verify(servicoJpaRepository, times(1)).findByPriceBetween(minPrice, maxPrice, pageable);
        verify(servicoMapper, times(1)).toDomainEntity(servicoJpaEntity);
    }

    @Test
    void shouldFindServicosByNomeOrDescriptionContaining() {
        // Given
        String searchTerm = "veterinária";
        Pageable pageable = PageRequest.of(0, 10);
        Page<ServicoJpaEntity> jpaPage = new PageImpl<>(Arrays.asList(servicoJpaEntity));
        
        when(servicoJpaRepository.findByNomeOrDescriptionContaining(searchTerm, pageable)).thenReturn(jpaPage);
        when(servicoMapper.toDomainEntity(servicoJpaEntity)).thenReturn(servico);

        // When
        Page<Servico> result = servicoRepositoryAdapter.findByNomeContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                searchTerm, searchTerm, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(servico.getId(), result.getContent().get(0).getId());
        
        verify(servicoJpaRepository, times(1)).findByNomeOrDescriptionContaining(searchTerm, pageable);
        verify(servicoMapper, times(1)).toDomainEntity(servicoJpaEntity);
    }

    @Test
    void shouldHandleEmptyListFromFindByVeterinarioId() {
        // Given
        when(servicoJpaRepository.findIdsByVeterinarioId(veterinarioId)).thenReturn(Arrays.asList());

        // When
        List<Servico> result = servicoRepositoryAdapter.findByVeterinarioId(veterinarioId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        verify(servicoJpaRepository, times(1)).findIdsByVeterinarioId(veterinarioId);
        verify(servicoMapper, never()).toDomainEntity(any());
    }

    @Test
    void shouldHandleNullReturnFromMapper() {
        // Given
        when(servicoJpaRepository.findById(servicoId)).thenReturn(Optional.of(servicoJpaEntity));
        when(servicoMapper.toDomainEntity(servicoJpaEntity)).thenReturn(null);

        // When
        Optional<Servico> result = servicoRepositoryAdapter.findById(servicoId);

        // Then
        assertTrue(result.isPresent());
        assertNull(result.get());
        
        verify(servicoJpaRepository, times(1)).findById(servicoId);
        verify(servicoMapper, times(1)).toDomainEntity(servicoJpaEntity);
    }

    @Test
    void shouldHandleExceptionDuringFindAll() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        when(servicoJpaRepository.findAll(pageable)).thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> servicoRepositoryAdapter.findAll(pageable));
        
        assertEquals("Database error", exception.getMessage());
        verify(servicoJpaRepository, times(1)).findAll(pageable);
    }

    @Test
    void shouldHandleExceptionDuringSave() {
        // Given
        when(servicoMapper.toJpaEntity(servico)).thenReturn(servicoJpaEntity);
        when(servicoJpaRepository.save(servicoJpaEntity)).thenThrow(new RuntimeException("Save failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> servicoRepositoryAdapter.save(servico));
        
        assertEquals("Save failed", exception.getMessage());
        verify(servicoMapper, times(1)).toJpaEntity(servico);
        verify(servicoJpaRepository, times(1)).save(servicoJpaEntity);
    }

    @Test
    void shouldVerifyCorrectMethodCallsOnComplexSearch() {
        // Given
        String searchTerm = "consulta";
        Pageable pageable = PageRequest.of(0, 5);
        Page<ServicoJpaEntity> jpaPage = new PageImpl<>(Arrays.asList(servicoJpaEntity));
        
        when(servicoJpaRepository.findByNomeOrDescriptionContaining(searchTerm, pageable)).thenReturn(jpaPage);
        when(servicoMapper.toDomainEntity(servicoJpaEntity)).thenReturn(servico);

        // When
        Page<Servico> result = servicoRepositoryAdapter.findByNomeContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                searchTerm, searchTerm, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(5, result.getSize());
        
        verify(servicoJpaRepository, times(1)).findByNomeOrDescriptionContaining(eq(searchTerm), eq(pageable));
        verify(servicoMapper, times(1)).toDomainEntity(servicoJpaEntity);
    }

    @Test
    void shouldHandleMultipleServicosForSameVeterinario() {
        // Given
        Servico servico2 = Servico.builder()
                .id(UUID.randomUUID())
                .veterinarioId(veterinarioId)
                .nome("Vacinação")
                .price(new BigDecimal("50.00"))
                .build();

        ServicoJpaEntity servicoJpaEntity2 = ServicoJpaEntity.builder()
                .veterinarioId(veterinarioId)
                .nome("Vacinação")
                .price(new BigDecimal("50.00"))
                .build();
        servicoJpaEntity2.setId(servico2.getId());

        List<UUID> servicoIds = Arrays.asList(servicoId, servico2.getId());

        when(servicoJpaRepository.findIdsByVeterinarioId(veterinarioId)).thenReturn(servicoIds);
        when(servicoJpaRepository.findById(servicoId)).thenReturn(Optional.of(servicoJpaEntity));
        when(servicoJpaRepository.findById(servico2.getId())).thenReturn(Optional.of(servicoJpaEntity2));
        when(servicoMapper.toDomainEntity(servicoJpaEntity)).thenReturn(servico);
        when(servicoMapper.toDomainEntity(servicoJpaEntity2)).thenReturn(servico2);

        // When
        List<Servico> result = servicoRepositoryAdapter.findByVeterinarioId(veterinarioId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(servico));
        assertTrue(result.contains(servico2));
        
        verify(servicoJpaRepository, times(1)).findIdsByVeterinarioId(veterinarioId);
        verify(servicoMapper, times(2)).toDomainEntity(any());
    }

    @Test
    void shouldVerifyPaginationParameters() {
        // Given
        Pageable customPageable = PageRequest.of(2, 5);
        Page<ServicoJpaEntity> jpaPage = new PageImpl<>(Arrays.asList(servicoJpaEntity), customPageable, 15);
        
        when(servicoJpaRepository.findAll(customPageable)).thenReturn(jpaPage);
        when(servicoMapper.toDomainEntity(servicoJpaEntity)).thenReturn(servico);

        // When
        Page<Servico> result = servicoRepositoryAdapter.findAll(customPageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getNumberOfElements());
        assertEquals(2, result.getNumber());
        assertEquals(5, result.getSize());
        assertEquals(15, result.getTotalElements());
        assertEquals(3, result.getTotalPages());
        
        verify(servicoJpaRepository, times(1)).findAll(customPageable);
    }
}
