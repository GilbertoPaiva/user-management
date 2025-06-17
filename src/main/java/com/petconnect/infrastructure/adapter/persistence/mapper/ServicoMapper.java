package com.petconnect.infrastructure.adapter.persistence.mapper;

import com.petconnect.domain.servico.entity.Servico;
import com.petconnect.infrastructure.adapter.persistence.entity.ServicoJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ServicoMapper {

    public ServicoJpaEntity toJpaEntity(Servico servico) {
        if (servico == null) return null;

        return ServicoJpaEntity.builder()
                .id(servico.getId())
                .veterinarioId(servico.getVeterinarioId())
                .nome(servico.getNome())
                .description(servico.getDescription())
                .price(servico.getPrice())
                .createdAt(servico.getCreatedAt())
                .updatedAt(servico.getUpdatedAt())
                .build();
    }

    public Servico toDomainEntity(ServicoJpaEntity jpaEntity) {
        if (jpaEntity == null) return null;

        return Servico.builder()
                .id(jpaEntity.getId())
                .veterinarioId(jpaEntity.getVeterinarioId())
                .nome(jpaEntity.getNome())
                .description(jpaEntity.getDescription())
                .price(jpaEntity.getPrice())
                .createdAt(jpaEntity.getCreatedAt())
                .updatedAt(jpaEntity.getUpdatedAt())
                .build();
    }
}
