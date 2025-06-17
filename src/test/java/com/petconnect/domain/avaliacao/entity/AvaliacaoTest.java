package com.petconnect.domain.avaliacao.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

class AvaliacaoTest {

    @Test
    void shouldCreateAvaliacaoWithRequiredFields() {
        // Given
        UUID entidadeId = UUID.randomUUID();
        UUID autorId = UUID.randomUUID();
        
        // When
        Avaliacao avaliacao = Avaliacao.builder()
                .entidadeId(entidadeId)
                .tipoEntidade(Avaliacao.TipoEntidade.PRODUTO)
                .autorId(autorId)
                .autorNome("João Silva")
                .nota(5)
                .comentario("Excelente produto!")
                .ativo(true)
                .build();
        
        // Then
        assertNotNull(avaliacao);
        assertEquals(entidadeId, avaliacao.getEntidadeId());
        assertEquals(Avaliacao.TipoEntidade.PRODUTO, avaliacao.getTipoEntidade());
        assertEquals(autorId, avaliacao.getAutorId());
        assertEquals("João Silva", avaliacao.getAutorNome());
        assertEquals(5, avaliacao.getNota());
        assertEquals("Excelente produto!", avaliacao.getComentario());
        assertTrue(avaliacao.isAtivo());
    }

    @Test
    void shouldValidateNotaRange() {
        // Given
        Avaliacao avaliacaoValida = Avaliacao.builder()
                .nota(3)
                .build();
        
        Avaliacao avaliacaoNotaMenor = Avaliacao.builder()
                .nota(0)
                .build();
        
        Avaliacao avaliacaoNotaMaior = Avaliacao.builder()
                .nota(6)
                .build();
        
        // When & Then
        assertDoesNotThrow(() -> avaliacaoValida.validarNota());
        
        IllegalArgumentException exceptionMenor = assertThrows(
            IllegalArgumentException.class, 
            () -> avaliacaoNotaMenor.validarNota()
        );
        assertEquals("Nota deve estar entre 1 e 5", exceptionMenor.getMessage());
        
        IllegalArgumentException exceptionMaior = assertThrows(
            IllegalArgumentException.class, 
            () -> avaliacaoNotaMaior.validarNota()
        );
        assertEquals("Nota deve estar entre 1 e 5", exceptionMaior.getMessage());
    }

    @Test
    void shouldValidateAllValidNotes() {
        // Given
        int[] notasValidas = {1, 2, 3, 4, 5};
        
        for (int nota : notasValidas) {
            // When
            Avaliacao avaliacao = Avaliacao.builder()
                    .nota(nota)
                    .build();
            
            // Then
            assertDoesNotThrow(() -> avaliacao.validarNota());
        }
    }

    @Test
    void shouldActivateAvaliacao() {
        // Given
        Avaliacao avaliacao = Avaliacao.builder()
                .ativo(false)
                .build();
        
        // When
        avaliacao.ativar();
        
        // Then
        assertTrue(avaliacao.isAtivo());
    }

    @Test
    void shouldDeactivateAvaliacao() {
        // Given
        Avaliacao avaliacao = Avaliacao.builder()
                .ativo(true)
                .build();
        
        // When
        avaliacao.desativar();
        
        // Then
        assertFalse(avaliacao.isAtivo());
    }

    @Test
    void shouldIdentifyExcellentNote() {
        // Given
        Avaliacao avaliacaoExcelente4 = Avaliacao.builder()
                .nota(4)
                .build();
        
        Avaliacao avaliacaoExcelente5 = Avaliacao.builder()
                .nota(5)
                .build();
        
        Avaliacao avaliacaoRegular = Avaliacao.builder()
                .nota(3)
                .build();
        
        // When & Then
        assertTrue(avaliacaoExcelente4.isNotaExcelente());
        assertTrue(avaliacaoExcelente5.isNotaExcelente());
        assertFalse(avaliacaoRegular.isNotaExcelente());
    }

    @Test
    void shouldIdentifyBadNote() {
        // Given
        Avaliacao avaliacaoRuim1 = Avaliacao.builder()
                .nota(1)
                .build();
        
        Avaliacao avaliacaoRuim2 = Avaliacao.builder()
                .nota(2)
                .build();
        
        Avaliacao avaliacaoRegular = Avaliacao.builder()
                .nota(3)
                .build();
        
        // When & Then
        assertTrue(avaliacaoRuim1.isNotaRuim());
        assertTrue(avaliacaoRuim2.isNotaRuim());
        assertFalse(avaliacaoRegular.isNotaRuim());
    }

    @Test
    void shouldWorkWithAllTipoEntidadeValues() {
        // Given
        Avaliacao.TipoEntidade[] allTypes = Avaliacao.TipoEntidade.values();
        
        for (Avaliacao.TipoEntidade tipo : allTypes) {
            // When
            Avaliacao avaliacao = Avaliacao.builder()
                    .tipoEntidade(tipo)
                    .nota(5)
                    .build();
            
            // Then
            assertNotNull(avaliacao);
            assertEquals(tipo, avaliacao.getTipoEntidade());
        }
    }

    @Test
    void shouldValidateTipoEntidadeEnumValues() {
        // Given
        Avaliacao.TipoEntidade[] expectedTypes = {
            Avaliacao.TipoEntidade.PRODUTO,
            Avaliacao.TipoEntidade.SERVICO,
            Avaliacao.TipoEntidade.VETERINARIO,
            Avaliacao.TipoEntidade.LOJISTA
        };
        
        // When
        Avaliacao.TipoEntidade[] actualTypes = Avaliacao.TipoEntidade.values();
        
        // Then
        assertEquals(expectedTypes.length, actualTypes.length);
        for (Avaliacao.TipoEntidade expectedType : expectedTypes) {
            assertTrue(java.util.Arrays.asList(actualTypes).contains(expectedType));
        }
    }

    @Test
    void shouldCreateAvaliacaoForEachTipoEntidade() {
        // Given & When
        Avaliacao avaliacaoProduto = Avaliacao.builder()
                .tipoEntidade(Avaliacao.TipoEntidade.PRODUTO)
                .nota(4)
                .build();
        
        Avaliacao avaliacaoServico = Avaliacao.builder()
                .tipoEntidade(Avaliacao.TipoEntidade.SERVICO)
                .nota(5)
                .build();
        
        Avaliacao avaliacaoVeterinario = Avaliacao.builder()
                .tipoEntidade(Avaliacao.TipoEntidade.VETERINARIO)
                .nota(3)
                .build();
        
        Avaliacao avaliacaoLojista = Avaliacao.builder()
                .tipoEntidade(Avaliacao.TipoEntidade.LOJISTA)
                .nota(2)
                .build();
        
        // Then
        assertEquals(Avaliacao.TipoEntidade.PRODUTO, avaliacaoProduto.getTipoEntidade());
        assertEquals(Avaliacao.TipoEntidade.SERVICO, avaliacaoServico.getTipoEntidade());
        assertEquals(Avaliacao.TipoEntidade.VETERINARIO, avaliacaoVeterinario.getTipoEntidade());
        assertEquals(Avaliacao.TipoEntidade.LOJISTA, avaliacaoLojista.getTipoEntidade());
    }

    @Test
    void shouldHandleNullComment() {
        // Given & When
        Avaliacao avaliacao = Avaliacao.builder()
                .nota(4)
                .comentario(null)
                .build();
        
        // Then
        assertNotNull(avaliacao);
        assertNull(avaliacao.getComentario());
    }

    @Test
    void shouldHandleEmptyComment() {
        // Given & When
        Avaliacao avaliacao = Avaliacao.builder()
                .nota(4)
                .comentario("")
                .build();
        
        // Then
        assertNotNull(avaliacao);
        assertEquals("", avaliacao.getComentario());
    }

    @Test
    void shouldHandleLongComment() {
        // Given
        String longComment = "Este é um comentário muito longo sobre o produto/serviço que foi avaliado. ".repeat(10);
        
        // When
        Avaliacao avaliacao = Avaliacao.builder()
                .nota(4)
                .comentario(longComment)
                .build();
        
        // Then
        assertNotNull(avaliacao);
        assertEquals(longComment, avaliacao.getComentario());
    }

    @Test
    void shouldHandleSpecialCharactersInComment() {
        // Given
        String commentWithSpecialChars = "Produto excelente! 5 estrelas ⭐⭐⭐⭐⭐ - Muito satisfeito 😊";
        
        // When
        Avaliacao avaliacao = Avaliacao.builder()
                .nota(5)
                .comentario(commentWithSpecialChars)
                .build();
        
        // Then
        assertNotNull(avaliacao);
        assertEquals(commentWithSpecialChars, avaliacao.getComentario());
    }

    @Test
    void shouldToggleAtivoStatusMultipleTimes() {
        // Given
        Avaliacao avaliacao = Avaliacao.builder()
                .ativo(true)
                .build();
        
        // When & Then
        assertTrue(avaliacao.isAtivo());
        
        avaliacao.desativar();
        assertFalse(avaliacao.isAtivo());
        
        avaliacao.ativar();
        assertTrue(avaliacao.isAtivo());
        
        avaliacao.desativar();
        assertFalse(avaliacao.isAtivo());
    }

    @Test
    void shouldClassifyNotesCorrectly() {
        // Given
        Avaliacao[] avaliacoes = {
            Avaliacao.builder().nota(1).build(),
            Avaliacao.builder().nota(2).build(),
            Avaliacao.builder().nota(3).build(),
            Avaliacao.builder().nota(4).build(),
            Avaliacao.builder().nota(5).build()
        };
        
        // When & Then
        assertTrue(avaliacoes[0].isNotaRuim());    // nota 1
        assertFalse(avaliacoes[0].isNotaExcelente());
        
        assertTrue(avaliacoes[1].isNotaRuim());    // nota 2
        assertFalse(avaliacoes[1].isNotaExcelente());
        
        assertFalse(avaliacoes[2].isNotaRuim());   // nota 3
        assertFalse(avaliacoes[2].isNotaExcelente());
        
        assertFalse(avaliacoes[3].isNotaRuim());   // nota 4
        assertTrue(avaliacoes[3].isNotaExcelente());
        
        assertFalse(avaliacoes[4].isNotaRuim());   // nota 5
        assertTrue(avaliacoes[4].isNotaExcelente());
    }

    @Test
    void shouldCreateAvaliacaoWithMinimalData() {
        // Given & When
        Avaliacao avaliacao = Avaliacao.builder()
                .entidadeId(UUID.randomUUID())
                .tipoEntidade(Avaliacao.TipoEntidade.PRODUTO)
                .autorId(UUID.randomUUID())
                .nota(3)
                .build();
        
        // Then
        assertNotNull(avaliacao);
        assertNotNull(avaliacao.getEntidadeId());
        assertNotNull(avaliacao.getTipoEntidade());
        assertNotNull(avaliacao.getAutorId());
        assertEquals(3, avaliacao.getNota());
        assertDoesNotThrow(() -> avaliacao.validarNota());
    }

    @Test
    void shouldMaintainDataIntegrityAfterStatusChanges() {
        // Given
        UUID entidadeId = UUID.randomUUID();
        UUID autorId = UUID.randomUUID();
        String autorNome = "João Silva";
        int nota = 5;
        String comentario = "Excelente!";
        
        Avaliacao avaliacao = Avaliacao.builder()
                .entidadeId(entidadeId)
                .tipoEntidade(Avaliacao.TipoEntidade.SERVICO)
                .autorId(autorId)
                .autorNome(autorNome)
                .nota(nota)
                .comentario(comentario)
                .ativo(true)
                .build();
        
        // When - Change status multiple times
        avaliacao.desativar();
        avaliacao.ativar();
        avaliacao.desativar();
        
        // Then - Other fields should remain unchanged
        assertEquals(entidadeId, avaliacao.getEntidadeId());
        assertEquals(Avaliacao.TipoEntidade.SERVICO, avaliacao.getTipoEntidade());
        assertEquals(autorId, avaliacao.getAutorId());
        assertEquals(autorNome, avaliacao.getAutorNome());
        assertEquals(nota, avaliacao.getNota());
        assertEquals(comentario, avaliacao.getComentario());
        assertFalse(avaliacao.isAtivo()); // Should be deactivated (last operation)
    }
}
