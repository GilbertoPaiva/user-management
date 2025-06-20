package com.petconnect.infrastructure.adapter.persistence.mapper;

import com.petconnect.domain.user.entity.Role;
import com.petconnect.infrastructure.adapter.persistence.entity.RoleJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    
    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

    Role toDomain(RoleJpaEntity jpaEntity);

    RoleJpaEntity toJpaEntity(Role domain);
} 