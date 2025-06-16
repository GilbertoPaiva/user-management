package com.petconnect.application.produto.usecase;

import com.petconnect.domain.produto.entity.Produto;
import com.petconnect.domain.produto.port.ProdutoRepositoryPort;
import com.petconnect.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateProdutoUseCase {
    
    private final ProdutoRepositoryPort produtoRepository;

    public Produto execute(CreateProdutoCommand command) {
        validateCommand(command);
        
        Produto produto = Produto.builder()
                .lojistaId(command.getLojistaId())
                .nome(command.getNome())
                .description(command.getDescription())
                .price(command.getPrice())
                .photoUrl(command.getPhotoUrl())
                .unitOfMeasure(command.getUnitOfMeasure())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return produtoRepository.save(produto);
    }

    private void validateCommand(CreateProdutoCommand command) {
        if (command.getNome() == null || command.getNome().trim().isEmpty()) {
            throw new BadRequestException("Nome do produto é obrigatório");
        }
        if (command.getPrice() == null || !(command.getPrice().signum() > 0)) {
            throw new BadRequestException("Preço deve ser maior que zero");
        }
        if (command.getLojistaId() == null) {
            throw new BadRequestException("ID do lojista é obrigatório");
        }
    }
}
