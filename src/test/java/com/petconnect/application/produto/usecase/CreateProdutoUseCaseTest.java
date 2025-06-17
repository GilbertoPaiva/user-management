package com.petconnect.application.produto.usecase;

import com.petconnect.domain.produto.entity.Produto;
import com.petconnect.domain.produto.port.ProdutoRepositoryPort;
import com.petconnect.exception.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateProdutoUseCaseTest {

    @Mock
    private ProdutoRepositoryPort produtoRepository;

    @InjectMocks
    private CreateProdutoUseCase createProdutoUseCase;

    private CreateProdutoCommand validCommand;
    private UUID lojistaId;

    @BeforeEach
    void setUp() {
        lojistaId = UUID.randomUUID();
        validCommand = CreateProdutoCommand.builder()
                .lojistaId(lojistaId)
                .nome("Ração Premium")
                .description("Ração premium para cães adultos")
                .price(new BigDecimal("29.99"))
                .photoUrl("https://example.com/photo.jpg")
                .unitOfMeasure("kg")
                .build();
    }

    @Test
    void shouldCreateProdutoSuccessfully() {
        // Given
        Produto savedProduto = Produto.builder()
                .id(UUID.randomUUID())
                .lojistaId(lojistaId)
                .nome("Ração Premium")
                .description("Ração premium para cães adultos")
                .price(new BigDecimal("29.99"))
                .photoUrl("https://example.com/photo.jpg")
                .unitOfMeasure("kg")
                .build();

        when(produtoRepository.save(any(Produto.class))).thenReturn(savedProduto);

        // When
        Produto result = createProdutoUseCase.execute(validCommand);

        // Then
        assertNotNull(result);
        assertEquals(lojistaId, result.getLojistaId());
        assertEquals("Ração Premium", result.getNome());
        assertEquals("Ração premium para cães adultos", result.getDescription());
        assertEquals(new BigDecimal("29.99"), result.getPrice());
        assertEquals("kg", result.getUnitOfMeasure());
        
        verify(produtoRepository, times(1)).save(any(Produto.class));
    }

    @Test
    void shouldThrowExceptionWhenNomeIsNull() {
        // Given
        CreateProdutoCommand invalidCommand = CreateProdutoCommand.builder()
                .lojistaId(lojistaId)
                .nome(null)
                .price(new BigDecimal("29.99"))
                .unitOfMeasure("kg")
                .build();

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class, 
                () -> createProdutoUseCase.execute(invalidCommand));
        
        assertEquals("Nome do produto é obrigatório", exception.getMessage());
        verify(produtoRepository, never()).save(any(Produto.class));
    }

    @Test
    void shouldThrowExceptionWhenNomeIsEmpty() {
        // Given
        CreateProdutoCommand invalidCommand = CreateProdutoCommand.builder()
                .lojistaId(lojistaId)
                .nome("   ")
                .price(new BigDecimal("29.99"))
                .unitOfMeasure("kg")
                .build();

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class, 
                () -> createProdutoUseCase.execute(invalidCommand));
        
        assertEquals("Nome do produto é obrigatório", exception.getMessage());
        verify(produtoRepository, never()).save(any(Produto.class));
    }

    @Test
    void shouldThrowExceptionWhenPriceIsNull() {
        // Given
        CreateProdutoCommand invalidCommand = CreateProdutoCommand.builder()
                .lojistaId(lojistaId)
                .nome("Ração Premium")
                .price(null)
                .unitOfMeasure("kg")
                .build();

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class, 
                () -> createProdutoUseCase.execute(invalidCommand));
        
        assertEquals("Preço deve ser maior que zero", exception.getMessage());
        verify(produtoRepository, never()).save(any(Produto.class));
    }

    @Test
    void shouldThrowExceptionWhenPriceIsZero() {
        // Given
        CreateProdutoCommand invalidCommand = CreateProdutoCommand.builder()
                .lojistaId(lojistaId)
                .nome("Ração Premium")
                .price(BigDecimal.ZERO)
                .unitOfMeasure("kg")
                .build();

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class, 
                () -> createProdutoUseCase.execute(invalidCommand));
        
        assertEquals("Preço deve ser maior que zero", exception.getMessage());
        verify(produtoRepository, never()).save(any(Produto.class));
    }

    @Test
    void shouldThrowExceptionWhenPriceIsNegative() {
        // Given
        CreateProdutoCommand invalidCommand = CreateProdutoCommand.builder()
                .lojistaId(lojistaId)
                .nome("Ração Premium")
                .price(new BigDecimal("-10.00"))
                .unitOfMeasure("kg")
                .build();

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class, 
                () -> createProdutoUseCase.execute(invalidCommand));
        
        assertEquals("Preço deve ser maior que zero", exception.getMessage());
        verify(produtoRepository, never()).save(any(Produto.class));
    }

    @Test
    void shouldThrowExceptionWhenLojistaIdIsNull() {
        // Given
        CreateProdutoCommand invalidCommand = CreateProdutoCommand.builder()
                .lojistaId(null)
                .nome("Ração Premium")
                .price(new BigDecimal("29.99"))
                .unitOfMeasure("kg")
                .build();

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class, 
                () -> createProdutoUseCase.execute(invalidCommand));
        
        assertEquals("ID do lojista é obrigatório", exception.getMessage());
        verify(produtoRepository, never()).save(any(Produto.class));
    }

    @Test
    void shouldCreateProdutoWithoutOptionalFields() {
        // Given
        CreateProdutoCommand minimalCommand = CreateProdutoCommand.builder()
                .lojistaId(lojistaId)
                .nome("Produto Básico")
                .price(new BigDecimal("10.00"))
                .unitOfMeasure("un")
                .build();

        Produto savedProduto = Produto.builder()
                .id(UUID.randomUUID())
                .lojistaId(lojistaId)
                .nome("Produto Básico")
                .price(new BigDecimal("10.00"))
                .unitOfMeasure("un")
                .build();

        when(produtoRepository.save(any(Produto.class))).thenReturn(savedProduto);

        // When
        Produto result = createProdutoUseCase.execute(minimalCommand);

        // Then
        assertNotNull(result);
        assertEquals("Produto Básico", result.getNome());
        assertNull(result.getDescription());
        assertNull(result.getPhotoUrl());
        
        verify(produtoRepository, times(1)).save(any(Produto.class));
    }

    @Test
    void shouldCreateProdutoWithDecimalPrice() {
        // Given
        validCommand.setPrice(new BigDecimal("15.99"));
        
        Produto savedProduto = Produto.builder()
                .id(UUID.randomUUID())
                .lojistaId(lojistaId)
                .nome("Ração Premium")
                .price(new BigDecimal("15.99"))
                .unitOfMeasure("kg")
                .build();

        when(produtoRepository.save(any(Produto.class))).thenReturn(savedProduto);

        // When
        Produto result = createProdutoUseCase.execute(validCommand);

        // Then
        assertNotNull(result);
        assertEquals(new BigDecimal("15.99"), result.getPrice());
        
        verify(produtoRepository, times(1)).save(any(Produto.class));
    }

    @Test
    void shouldVerifyProdutoFieldsAreSetCorrectly() {
        // Given
        when(produtoRepository.save(any(Produto.class))).thenAnswer(invocation -> {
            Produto produto = invocation.getArgument(0);
            // Verify that created and updated timestamps are set
            assertNotNull(produto.getCreatedAt());
            assertNotNull(produto.getUpdatedAt());
            return produto;
        });

        // When
        createProdutoUseCase.execute(validCommand);

        // Then
        verify(produtoRepository, times(1)).save(argThat(produto -> 
            produto.getLojistaId().equals(lojistaId) &&
            produto.getNome().equals("Ração Premium") &&
            produto.getDescription().equals("Ração premium para cães adultos") &&
            produto.getPrice().equals(new BigDecimal("29.99")) &&
            produto.getPhotoUrl().equals("https://example.com/photo.jpg") &&
            produto.getUnitOfMeasure().equals("kg") &&
            produto.getCreatedAt() != null &&
            produto.getUpdatedAt() != null
        ));
    }
}
