package com.userauth.usermanagement.domain.user.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {
    private String nome;
    private String location;
    private String contactNumber;
    private String cnpj;
    private String crmv;
    private String storeType;
    private String businessHours;
    private String guardian;
}
