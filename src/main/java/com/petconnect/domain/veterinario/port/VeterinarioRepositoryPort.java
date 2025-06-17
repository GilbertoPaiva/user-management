package com.petconnect.domain.veterinario.port;

import com.petconnect.domain.veterinario.entity.Veterinario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VeterinarioRepositoryPort {
    Veterinario save(Veterinario veterinario);
    Optional<Veterinario> findById(UUID id);
    Optional<Veterinario> findByUserId(UUID userId);
    List<Veterinario> findAll();
    Page<Veterinario> findAll(Pageable pageable);
    void deleteById(UUID id);
    boolean existsByUserId(UUID userId);
}
