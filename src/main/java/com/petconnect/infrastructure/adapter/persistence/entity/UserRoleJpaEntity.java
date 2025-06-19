package com.petconnect.infrastructure.adapter.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "user_roles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleJpaEntity {
    
    @EmbeddedId
    private UserRoleId id;
    
    public static class UserRoleId implements java.io.Serializable {
        @Column(name = "user_id")
        private UUID userId;
        
        @Column(name = "role_id") 
        private UUID roleId;
        
        public UserRoleId() {}
        
        public UserRoleId(UUID userId, UUID roleId) {
            this.userId = userId;
            this.roleId = roleId;
        }
        
        public UUID getUserId() { return userId; }
        public void setUserId(UUID userId) { this.userId = userId; }
        public UUID getRoleId() { return roleId; }
        public void setRoleId(UUID roleId) { this.roleId = roleId; }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof UserRoleId)) return false;
            UserRoleId that = (UserRoleId) o;
            return java.util.Objects.equals(userId, that.userId) && 
                   java.util.Objects.equals(roleId, that.roleId);
        }
        
        @Override
        public int hashCode() {
            return java.util.Objects.hash(userId, roleId);
        }
    }
}
