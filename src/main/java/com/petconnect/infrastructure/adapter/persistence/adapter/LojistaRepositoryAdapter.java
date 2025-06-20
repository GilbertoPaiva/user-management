package com.petconnect.infrastructure.adapter.persistence.adapter;

import com.petconnect.domain.lojista.entity.Lojista;
import com.petconnect.domain.lojista.port.LojistaRepositoryPort;
import com.petconnect.infrastructure.adapter.persistence.entity.LojistaJpaEntity;
import com.petconnect.infrastructure.adapter.persistence.mapper.LojistaMapper;
import com.petconnect.infrastructure.adapter.persistence.repository.LojistaJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class LojistaRepositoryAdapter implements LojistaRepositoryPort {
    private final LojistaJpaRepository jpaRepository;
    private final LojistaMapper mapper;

    @Override
    public Lojista save(Lojista lojista) {
        LojistaJpaEntity entity = mapper.toEntity(lojista);
        LojistaJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public List<Lojista> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
    }
} 