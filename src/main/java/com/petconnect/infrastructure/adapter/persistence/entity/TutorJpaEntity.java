package com.petconnect.infrastructure.adapter.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tutores")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TutorJpaEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    
    @Column(nullable = false)
    private String nome;
    
    @Column(name = "cnpj_opcional")
    private String cnpjOpcional;
    
    private String location;
    
    @Column(name = "contact_number")
    private String contactNumber;
    
    private String guardian;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
