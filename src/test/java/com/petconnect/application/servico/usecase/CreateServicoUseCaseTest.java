package com.petconnect.application.servico.usecase;

import com.petconnect.domain.servico.entity.Servico;
import com.petconnect.domain.servico.port.ServicoRepositoryPort;
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
class CreateServicoUseCaseTest {

    @Mock
    private ServicoRepositoryPort servicoRepository;

    @InjectMocks
    private CreateServicoUseCase createServicoUseCase;

    private CreateServicoCommand validCommand;
    private UUID veterinarioId;

    @BeforeEach
    void setUp() {
        veterinarioId = UUID.randomUUID();
        validCommand = CreateServicoCommand.builder()
                .veterinarioId(veterinarioId)
                .nome("Consulta Veterinária")
                .description("Consulta clínica geral para pets")
                .price(new BigDecimal("80.00"))
                .build();
    }

    @Test
    void shouldCreateServicoSuccessfully() {

        Servico savedServico = Servico.builder()
                .id(UUID.randomUUID())
                .veterinarioId(veterinarioId)
                .nome("Consulta Veterinária")
                .description("Consulta clínica geral para pets")
                .price(new BigDecimal("80.00"))
                .build();

        when(servicoRepository.save(any(Servico.class))).thenReturn(savedServico);


        Servico result = createServicoUseCase.execute(validCommand);


        assertNotNull(result);
        assertEquals(veterinarioId, result.getVeterinarioId());
        assertEquals("Consulta Veterinária", result.getNome());
        assertEquals("Consulta clínica geral para pets", result.getDescription());
        assertEquals(new BigDecimal("80.00"), result.getPrice());
        
        verify(servicoRepository, times(1)).save(any(Servico.class));
    }

    @Test
    void shouldThrowExceptionWhenNomeIsNull() {

        CreateServicoCommand invalidCommand = CreateServicoCommand.builder()
                .veterinarioId(veterinarioId)
                .nome(null)
                .price(new BigDecimal("80.00"))
                .build();


        BadRequestException exception = assertThrows(BadRequestException.class, 
                () -> createServicoUseCase.execute(invalidCommand));
        
        assertEquals("Nome do serviço é obrigatório", exception.getMessage());
        verify(servicoRepository, never()).save(any(Servico.class));
    }

    @Test
    void shouldThrowExceptionWhenNomeIsEmpty() {

        CreateServicoCommand invalidCommand = CreateServicoCommand.builder()
                .veterinarioId(veterinarioId)
                .nome("   ")
                .price(new BigDecimal("80.00"))
                .build();


        BadRequestException exception = assertThrows(BadRequestException.class, 
                () -> createServicoUseCase.execute(invalidCommand));
        
        assertEquals("Nome do serviço é obrigatório", exception.getMessage());
        verify(servicoRepository, never()).save(any(Servico.class));
    }

    @Test
    void shouldThrowExceptionWhenPriceIsNull() {

        CreateServicoCommand invalidCommand = CreateServicoCommand.builder()
                .veterinarioId(veterinarioId)
                .nome("Consulta Veterinária")
                .price(null)
                .build();


        BadRequestException exception = assertThrows(BadRequestException.class, 
                () -> createServicoUseCase.execute(invalidCommand));
        
        assertEquals("Preço deve ser um valor válido", exception.getMessage());
        verify(servicoRepository, never()).save(any(Servico.class));
    }

    @Test
    void shouldThrowExceptionWhenPriceIsInvalidFormat() {
        // Testing the regex validation for price format
        CreateServicoCommand invalidCommand = CreateServicoCommand.builder()
                .veterinarioId(veterinarioId)
                .nome("Consulta Veterinária")
                .price(new BigDecimal("80.999")) // More than 2 decimal places
                .build();


        BadRequestException exception = assertThrows(BadRequestException.class, 
                () -> createServicoUseCase.execute(invalidCommand));
        
        assertEquals("Preço deve ser um valor válido", exception.getMessage());
        verify(servicoRepository, never()).save(any(Servico.class));
    }

    @Test
    void shouldThrowExceptionWhenVeterinarioIdIsNull() {

        CreateServicoCommand invalidCommand = CreateServicoCommand.builder()
                .veterinarioId(null)
                .nome("Consulta Veterinária")
                .price(new BigDecimal("80.00"))
                .build();


        BadRequestException exception = assertThrows(BadRequestException.class, 
                () -> createServicoUseCase.execute(invalidCommand));
        
        assertEquals("ID do veterinário é obrigatório", exception.getMessage());
        verify(servicoRepository, never()).save(any(Servico.class));
    }

    @Test
    void shouldCreateServicoWithoutOptionalDescription() {

        CreateServicoCommand minimalCommand = CreateServicoCommand.builder()
                .veterinarioId(veterinarioId)
                .nome("Vacinação")
                .price(new BigDecimal("50.00"))
                .build();

        Servico savedServico = Servico.builder()
                .id(UUID.randomUUID())
                .veterinarioId(veterinarioId)
                .nome("Vacinação")
                .price(new BigDecimal("50.00"))
                .build();

        when(servicoRepository.save(any(Servico.class))).thenReturn(savedServico);


        Servico result = createServicoUseCase.execute(minimalCommand);


        assertNotNull(result);
        assertEquals("Vacinação", result.getNome());
        assertNull(result.getDescription());
        
        verify(servicoRepository, times(1)).save(any(Servico.class));
    }

    @Test
    void shouldAcceptValidPriceFormats() {
        // Testing valid price formats
        CreateServicoCommand[] validCommands = {
            CreateServicoCommand.builder()
                .veterinarioId(veterinarioId)
                .nome("Serviço 1")
                .price(new BigDecimal("100"))
                .build(),
            CreateServicoCommand.builder()
                .veterinarioId(veterinarioId)
                .nome("Serviço 2")
                .price(new BigDecimal("100.5"))
                .build(),
            CreateServicoCommand.builder()
                .veterinarioId(veterinarioId)
                .nome("Serviço 3")
                .price(new BigDecimal("100.50"))
                .build()
        };

        when(servicoRepository.save(any(Servico.class))).thenReturn(
            Servico.builder().id(UUID.randomUUID()).build()
        );


        for (CreateServicoCommand command : validCommands) {
            assertDoesNotThrow(() -> createServicoUseCase.execute(command));
        }
        
        verify(servicoRepository, times(3)).save(any(Servico.class));
    }

    @Test
    void shouldVerifyServicoFieldsAreSetCorrectly() {

        when(servicoRepository.save(any(Servico.class))).thenAnswer(invocation -> {
            Servico servico = invocation.getArgument(0);
            // Verify that ID, created and updated timestamps are set
            assertNotNull(servico.getId());
            assertNotNull(servico.getCreatedAt());
            assertNotNull(servico.getUpdatedAt());
            return servico;
        });


        createServicoUseCase.execute(validCommand);


        verify(servicoRepository, times(1)).save(argThat(servico -> 
            servico.getVeterinarioId().equals(veterinarioId) &&
            servico.getNome().equals("Consulta Veterinária") &&
            servico.getDescription().equals("Consulta clínica geral para pets") &&
            servico.getPrice().equals(new BigDecimal("80.00")) &&
            servico.getId() != null &&
            servico.getCreatedAt() != null &&
            servico.getUpdatedAt() != null
        ));
    }

    @Test
    void shouldGenerateUniqueIdForEachServico() {

        when(servicoRepository.save(any(Servico.class))).thenAnswer(invocation -> invocation.getArgument(0));


        Servico servico1 = createServicoUseCase.execute(validCommand);
        Servico servico2 = createServicoUseCase.execute(validCommand);


        assertNotNull(servico1.getId());
        assertNotNull(servico2.getId());
        assertNotEquals(servico1.getId(), servico2.getId());
    }

    @Test
    void shouldAcceptZeroPriceIfMatchesRegex() {
        // Test edge case where 0 might be valid according to regex
        CreateServicoCommand zeroCommand = CreateServicoCommand.builder()
                .veterinarioId(veterinarioId)
                .nome("Consulta Gratuita")
                .price(new BigDecimal("0"))
                .build();

        when(servicoRepository.save(any(Servico.class))).thenReturn(
            Servico.builder().id(UUID.randomUUID()).build()
        );


        assertDoesNotThrow(() -> createServicoUseCase.execute(zeroCommand));
        verify(servicoRepository, times(1)).save(any(Servico.class));
    }

    @Test
    void shouldHandleLargeValidPrices() {

        CreateServicoCommand expensiveCommand = CreateServicoCommand.builder()
                .veterinarioId(veterinarioId)
                .nome("Cirurgia Complexa")
                .price(new BigDecimal("9999.99"))
                .build();

        when(servicoRepository.save(any(Servico.class))).thenReturn(
            Servico.builder().id(UUID.randomUUID()).build()
        );


        assertDoesNotThrow(() -> createServicoUseCase.execute(expensiveCommand));
        verify(servicoRepository, times(1)).save(any(Servico.class));
    }
}
