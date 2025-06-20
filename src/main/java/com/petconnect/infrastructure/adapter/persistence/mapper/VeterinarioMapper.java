package com.petconnect.infrastructure.adapter.persistence.mapper;

import com.petconnect.domain.veterinario.entity.Veterinario;
import com.petconnect.infrastructure.adapter.persistence.entity.VeterinarioJpaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VeterinarioMapper {
    VeterinarioJpaEntity toEntity(Veterinario veterinario);
    Veterinario toDomain(VeterinarioJpaEntity entity);
} 