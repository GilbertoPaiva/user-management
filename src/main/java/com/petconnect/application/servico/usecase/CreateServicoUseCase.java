package com.petconnect.application.servico.usecase;

import com.petconnect.domain.servico.entity.Servico;
import com.petconnect.domain.servico.port.ServicoRepositoryPort;
import com.petconnect.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateServicoUseCase {
    
    private final ServicoRepositoryPort servicoRepository;

    public Servico execute(CreateServicoCommand command) {
        validateCommand(command);

        Servico servico = Servico.builder()
                .id(UUID.randomUUID())
                .veterinarioId(command.getVeterinarioId())
                .nome(command.getNome())
                .description(command.getDescription())
                .price(command.getPrice())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return servicoRepository.save(servico);
    }

    private void validateCommand(CreateServicoCommand command) {
        if (command.getNome() == null || command.getNome().trim().isEmpty()) {
            throw new BadRequestException("Nome do serviço é obrigatório");
        }
        
        if (command.getPrice() == null || !command.getPrice().toString().matches("\\d+(\\.\\d{1,2})?")) {
            throw new BadRequestException("Preço deve ser um valor válido");
        }
        
        if (command.getVeterinarioId() == null) {
            throw new BadRequestException("ID do veterinário é obrigatório");
        }
    }
}
