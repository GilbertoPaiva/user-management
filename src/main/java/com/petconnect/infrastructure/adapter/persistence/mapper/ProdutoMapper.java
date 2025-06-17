package com.petconnect.infrastructure.adapter.persistence.mapper;

import com.petconnect.domain.produto.entity.Produto;
import com.petconnect.infrastructure.adapter.persistence.entity.ProdutoJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ProdutoMapper {

    public ProdutoJpaEntity toJpaEntity(Produto produto) {
                if (produto == null) return null;

        ProdutoJpaEntity entity = ProdutoJpaEntity.builder()
                .lojistaId(produto.getLojistaId())
                .nome(produto.getNome())
                .description(produto.getDescription())
                .price(produto.getPrice())
                .photoUrl(produto.getPhotoUrl())
                .unitOfMeasure(produto.getUnitOfMeasure())
                .build();
        
        if (produto.getId() != null) {
            entity.setId(produto.getId());
        }
        
        return entity;
    }

    public Produto toDomainEntity(ProdutoJpaEntity jpaEntity) {
        if (jpaEntity == null) return null;

                return Produto.builder()
                .id(jpaEntity.getId())
                .lojistaId(jpaEntity.getLojistaId())
                .nome(jpaEntity.getNome())
                .description(jpaEntity.getDescription())
                .price(jpaEntity.getPrice())
                .photoUrl(jpaEntity.getPhotoUrl())
                .unitOfMeasure(jpaEntity.getUnitOfMeasure())
                .createdAt(jpaEntity.getCreatedAt())
                .updatedAt(jpaEntity.getUpdatedAt())
                .build();
    }
}
