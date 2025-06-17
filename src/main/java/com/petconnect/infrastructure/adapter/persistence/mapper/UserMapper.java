package com.petconnect.infrastructure.adapter.persistence.mapper;

import com.petconnect.domain.user.entity.SecurityQuestions;
import com.petconnect.domain.user.entity.User;
import com.petconnect.domain.user.entity.UserProfile;
import com.petconnect.infrastructure.adapter.persistence.entity.UserJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

        public UserJpaEntity toJpaEntity(User user) {
        if (user == null) return null;

        UserJpaEntity.UserJpaEntityBuilder builder = UserJpaEntity.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .fullName(user.getFullName())
                .userType(user.getUserType())
                .active(user.isActive())
                .roles(user.getRoles());

        if (user.getSecurityQuestions() != null) {
            SecurityQuestions sq = user.getSecurityQuestions();
            builder.securityQuestion1(sq.getQuestion1())
                   .securityAnswer1(sq.getAnswer1())
                   .securityQuestion2(sq.getQuestion2())
                   .securityAnswer2(sq.getAnswer2())
                   .securityQuestion3(sq.getQuestion3())
                                      .securityAnswer3(sq.getAnswer3());
        }

        if (user.getUserProfile() != null) {
            UserProfile up = user.getUserProfile();
            builder.nome(up.getNome())
                   .location(up.getLocation())
                   .contactNumber(up.getContactNumber())
                   .cnpj(up.getCnpj())
                   .crmv(up.getCrmv())
                   .storeType(up.getStoreType())
                   .businessHours(up.getBusinessHours())
                   .guardian(up.getGuardian());
        }

        UserJpaEntity entity = builder.build();
        
        if (user.getId() != null) {
            entity.setId(user.getId());
        }
        
        return entity;
    }

    public User toDomainEntity(UserJpaEntity jpaEntity) {
        if (jpaEntity == null) return null;

        SecurityQuestions securityQuestions = SecurityQuestions.builder()
                .question1(jpaEntity.getSecurityQuestion1())
                .answer1(jpaEntity.getSecurityAnswer1())
                .question2(jpaEntity.getSecurityQuestion2())
                .answer2(jpaEntity.getSecurityAnswer2())
                .question3(jpaEntity.getSecurityQuestion3())
                .answer3(jpaEntity.getSecurityAnswer3())
                .build();

        UserProfile userProfile = UserProfile.builder()
                .nome(jpaEntity.getNome())
                .location(jpaEntity.getLocation())
                .contactNumber(jpaEntity.getContactNumber())
                .cnpj(jpaEntity.getCnpj())
                .crmv(jpaEntity.getCrmv())
                .storeType(jpaEntity.getStoreType())
                .businessHours(jpaEntity.getBusinessHours())
                .guardian(jpaEntity.getGuardian())
                .build();

        return User.builder()
                .id(jpaEntity.getId())
                .username(jpaEntity.getUsername())
                .email(jpaEntity.getEmail())
                .password(jpaEntity.getPassword())
                .fullName(jpaEntity.getFullName())
                .userType(jpaEntity.getUserType())
                .active(jpaEntity.getActive())
                .roles(jpaEntity.getRoles())
                .securityQuestions(securityQuestions)
                .userProfile(userProfile)
                .createdAt(jpaEntity.getCreatedAt())
                .updatedAt(jpaEntity.getUpdatedAt())
                .build();
    }
}
