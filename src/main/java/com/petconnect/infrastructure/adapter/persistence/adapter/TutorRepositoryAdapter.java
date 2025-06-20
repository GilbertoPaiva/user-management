package com.petconnect.infrastructure.adapter.persistence.adapter;

import com.petconnect.domain.tutor.entity.Tutor;
import com.petconnect.domain.tutor.port.TutorRepositoryPort;
import com.petconnect.infrastructure.adapter.persistence.entity.TutorJpaEntity;
import com.petconnect.infrastructure.adapter.persistence.mapper.TutorMapper;
import com.petconnect.infrastructure.adapter.persistence.repository.TutorJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class TutorRepositoryAdapter implements TutorRepositoryPort {
    private final TutorJpaRepository jpaRepository;
    private final TutorMapper mapper;

    @Override
    public Tutor save(Tutor tutor) {
        TutorJpaEntity entity = mapper.toEntity(tutor);
        TutorJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public List<Tutor> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
    }
} 