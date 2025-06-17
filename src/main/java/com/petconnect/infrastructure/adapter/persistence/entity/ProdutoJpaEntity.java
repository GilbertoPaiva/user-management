package com.petconnect.infrastructure.adapter.persistence.entity;

import com.petconnect.domain.shared.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "produtos")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoJpaEntity extends AuditableEntity {
    
    @Column(name = "lojista_id", nullable = false)
    private UUID lojistaId;
    
    @Column(nullable = false)
    private String nome;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(name = "photo_url")
    private String photoUrl;
    
    @Column(name = "unit_of_measure")
    private String unitOfMeasure;
}
