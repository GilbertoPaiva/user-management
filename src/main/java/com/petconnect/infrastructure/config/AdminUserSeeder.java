package com.petconnect.infrastructure.config;

import com.petconnect.application.user.usecase.CreateUserCommand;
import com.petconnect.application.user.usecase.CreateUserUseCase;
import com.petconnect.domain.user.entity.UserType;
import com.petconnect.domain.user.port.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class AdminUserSeeder implements CommandLineRunner {

    private final CreateUserUseCase createUserUseCase;
    private final UserRepositoryPort userRepository;

    @Override
    public void run(String... args) throws Exception {
        createAdminUserIfNotExists();
        createTutorUserIfNotExists();
    }

    private void createAdminUserIfNotExists() {
        if (userRepository.findByUsername("admin").isEmpty()) {
            String adminEmail = "admin@petconnect.com";
            CreateUserCommand command = CreateUserCommand.builder()
                    .username("admin")
                    .email(adminEmail)
                    .password("Admin@1234")
                    .fullName("Administrador do Sistema")
                    .userType(UserType.ADMIN)
                    .roles(Set.of(UserType.ADMIN.name()))
                    .build();
            
            createUserUseCase.execute(command);

            System.out.println("=============================================");
            System.out.println("✅ USUÁRIO ADMIN CRIADO COM SUCESSO!");
            System.out.println("   Email: " + adminEmail);
            System.out.println("   Senha: Admin@1234");
            System.out.println("=============================================");
        } else {
            System.out.println("=============================================");
            System.out.println("ℹ️ Usuário admin já existe no sistema.");
            System.out.println("=============================================");
        }
    }

    private void createTutorUserIfNotExists() {
        if (userRepository.findByUsername("tutor").isEmpty()) {
            String tutorEmail = "tutor@petconnect.com";
            CreateUserCommand command = CreateUserCommand.builder()
                    .username("tutor")
                    .email(tutorEmail)
                    .password("Tutor@1234")
                    .fullName("Tutor de Teste")
                    .userType(UserType.TUTOR)
                    .roles(Set.of(UserType.TUTOR.name()))
                    .build();
            
            createUserUseCase.execute(command);

            System.out.println("=============================================");
            System.out.println("✅ USUÁRIO TUTOR CRIADO COM SUCESSO!");
            System.out.println("   Email: " + tutorEmail);
            System.out.println("   Senha: Tutor@1234");
            System.out.println("=============================================");
        } else {
            System.out.println("=============================================");
            System.out.println("ℹ️ Usuário tutor já existe no sistema.");
            System.out.println("=============================================");
        }
    }
} 