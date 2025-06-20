package com.petconnect.infrastructure.adapter.persistence.mapper;

import com.petconnect.domain.lojista.entity.Lojista;
import com.petconnect.infrastructure.adapter.persistence.entity.LojistaJpaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LojistaMapper {
    LojistaJpaEntity toEntity(Lojista lojista);
    Lojista toDomain(LojistaJpaEntity entity);
} 