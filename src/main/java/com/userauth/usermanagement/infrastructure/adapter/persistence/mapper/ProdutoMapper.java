package com.userauth.usermanagement.infrastructure.adapter.persistence.mapper;

import com.userauth.usermanagement.domain.produto.entity.Produto;
import com.userauth.usermanagement.infrastructure.adapter.persistence.entity.ProdutoJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ProdutoMapper {

    public ProdutoJpaEntity toJpaEntity(Produto produto) {
        if (produto == null) return null;

        return ProdutoJpaEntity.builder()
                .id(produto.getId())
                .lojistaId(produto.getLojistaId())
                .nome(produto.getNome())
                .description(produto.getDescription())
                .price(produto.getPrice())
                .photoUrl(produto.getPhotoUrl())
                .unitOfMeasure(produto.getUnitOfMeasure())
                .createdAt(produto.getCreatedAt())
                .updatedAt(produto.getUpdatedAt())
                .build();
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
