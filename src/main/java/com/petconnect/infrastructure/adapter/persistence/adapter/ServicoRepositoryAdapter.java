package com.petconnect.infrastructure.adapter.persistence.adapter;

import com.petconnect.domain.servico.entity.Servico;
import com.petconnect.domain.servico.port.ServicoRepositoryPort;
import com.petconnect.infrastructure.adapter.persistence.entity.ServicoJpaEntity;
import com.petconnect.infrastructure.adapter.persistence.mapper.ServicoMapper;
import com.petconnect.infrastructure.adapter.persistence.repository.ServicoJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ServicoRepositoryAdapter implements ServicoRepositoryPort {

    private final ServicoJpaRepository servicoJpaRepository;
    private final ServicoMapper servicoMapper;

    @Override
    public Servico save(Servico servico) {
        ServicoJpaEntity jpaEntity = servicoMapper.toJpaEntity(servico);
        ServicoJpaEntity savedEntity = servicoJpaRepository.save(jpaEntity);
        return servicoMapper.toDomainEntity(savedEntity);
    }

    @Override
    public Optional<Servico> findById(UUID id) {
        return servicoJpaRepository.findById(id)
                .map(servicoMapper::toDomainEntity);
    }

    @Override
    public List<Servico> findByVeterinarioId(UUID veterinarioId) {
        List<UUID> servicoIds = servicoJpaRepository.findIdsByVeterinarioId(veterinarioId);
        return servicoIds.stream()
                .map(id -> servicoJpaRepository.findById(id))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(servicoMapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Page<Servico> findAll(Pageable pageable) {
        return servicoJpaRepository.findAll(pageable)
                .map(servicoMapper::toDomainEntity);
    }

    @Override
    public Page<Servico> findByVeterinarioId(UUID veterinarioId, Pageable pageable) {
        return servicoJpaRepository.findByVeterinarioId(veterinarioId, pageable)
                .map(servicoMapper::toDomainEntity);
    }

    @Override
    public void deleteById(UUID id) {
        servicoJpaRepository.deleteById(id);
    }

    @Override
    public Page<Servico> findByNomeContainingIgnoreCase(String nome, Pageable pageable) {
        return servicoJpaRepository.findByNomeContainingIgnoreCase(nome, pageable)
                .map(servicoMapper::toDomainEntity);
    }

    @Override
    public Page<Servico> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return servicoJpaRepository.findByPriceBetween(minPrice, maxPrice, pageable)
                .map(servicoMapper::toDomainEntity);
    }

    @Override
    public Page<Servico> findByNomeContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String nome, String description, Pageable pageable) {
        return servicoJpaRepository.findByNomeOrDescriptionContaining(nome, pageable)
                .map(servicoMapper::toDomainEntity);
    }
}
