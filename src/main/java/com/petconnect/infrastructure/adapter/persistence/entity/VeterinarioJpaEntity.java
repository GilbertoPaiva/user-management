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
@Table(name = "veterinarios")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VeterinarioJpaEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    
    @Column(nullable = false)
    private String nome;
    
    @Column(unique = true, nullable = false)
    private String crmv;
    
    private String location;
    
    @Column(name = "contact_number")
    private String contactNumber;
    
    @Column(name = "business_hours")
    private String businessHours;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
