package com.petconnect.infrastructure.adapter.persistence.mapper;

import com.petconnect.domain.tutor.entity.Tutor;
import com.petconnect.infrastructure.adapter.persistence.entity.TutorJpaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TutorMapper {
    TutorJpaEntity toEntity(Tutor tutor);
    Tutor toDomain(TutorJpaEntity entity);
} 