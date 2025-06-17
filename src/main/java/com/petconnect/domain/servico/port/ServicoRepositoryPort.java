package com.petconnect.domain.servico.port;

import com.petconnect.domain.servico.entity.Servico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServicoRepositoryPort {
    Servico save(Servico servico);
    Optional<Servico> findById(UUID id);
    List<Servico> findByVeterinarioId(UUID veterinarioId);
    Page<Servico> findByVeterinarioId(UUID veterinarioId, Pageable pageable);
    void deleteById(UUID id);
    Page<Servico> findAll(Pageable pageable);
    

    Page<Servico> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
    Page<Servico> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    Page<Servico> findByNomeContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
        String nome, String description, Pageable pageable);
}
