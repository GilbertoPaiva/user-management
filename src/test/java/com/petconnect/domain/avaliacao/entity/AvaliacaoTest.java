package com.petconnect.domain.avaliacao.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

class AvaliacaoTest {

    @Test
    void shouldCreateAvaliacaoWithRequiredFields() {

        UUID entidadeId = UUID.randomUUID();
        UUID autorId = UUID.randomUUID();
        

        Avaliacao avaliacao = Avaliacao.builder()
                .entidadeId(entidadeId)
                .tipoEntidade(Avaliacao.TipoEntidade.PRODUTO)
                .autorId(autorId)
                .autorNome("João Silva")
                .nota(5)
                .comentario("Excelente produto!")
                .ativo(true)
                .build();
        

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

        Avaliacao avaliacaoValida = Avaliacao.builder()
                .nota(3)
                .build();
        
        Avaliacao avaliacaoNotaMenor = Avaliacao.builder()
                .nota(0)
                .build();
        
        Avaliacao avaliacaoNotaMaior = Avaliacao.builder()
                .nota(6)
                .build();
        

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

        int[] notasValidas = {1, 2, 3, 4, 5};
        
        for (int nota : notasValidas) {
    
            Avaliacao avaliacao = Avaliacao.builder()
                    .nota(nota)
                    .build();
            
    
            assertDoesNotThrow(() -> avaliacao.validarNota());
        }
    }

    @Test
    void shouldActivateAvaliacao() {

        Avaliacao avaliacao = Avaliacao.builder()
                .ativo(false)
                .build();
        

        avaliacao.ativar();
        

        assertTrue(avaliacao.isAtivo());
    }

    @Test
    void shouldDeactivateAvaliacao() {

        Avaliacao avaliacao = Avaliacao.builder()
                .ativo(true)
                .build();
        

        avaliacao.desativar();
        

        assertFalse(avaliacao.isAtivo());
    }

    @Test
    void shouldIdentifyExcellentNote() {

        Avaliacao avaliacaoExcelente4 = Avaliacao.builder()
                .nota(4)
                .build();
        
        Avaliacao avaliacaoExcelente5 = Avaliacao.builder()
                .nota(5)
                .build();
        
        Avaliacao avaliacaoRegular = Avaliacao.builder()
                .nota(3)
                .build();
        

        assertTrue(avaliacaoExcelente4.isNotaExcelente());
        assertTrue(avaliacaoExcelente5.isNotaExcelente());
        assertFalse(avaliacaoRegular.isNotaExcelente());
    }

    @Test
    void shouldIdentifyBadNote() {

        Avaliacao avaliacaoRuim1 = Avaliacao.builder()
                .nota(1)
                .build();
        
        Avaliacao avaliacaoRuim2 = Avaliacao.builder()
                .nota(2)
                .build();
        
        Avaliacao avaliacaoRegular = Avaliacao.builder()
                .nota(3)
                .build();
        

        assertTrue(avaliacaoRuim1.isNotaRuim());
        assertTrue(avaliacaoRuim2.isNotaRuim());
        assertFalse(avaliacaoRegular.isNotaRuim());
    }

    @Test
    void shouldWorkWithAllTipoEntidadeValues() {

        Avaliacao.TipoEntidade[] allTypes = Avaliacao.TipoEntidade.values();
        
        for (Avaliacao.TipoEntidade tipo : allTypes) {
    
            Avaliacao avaliacao = Avaliacao.builder()
                    .tipoEntidade(tipo)
                    .nota(5)
                    .build();
            
    
            assertNotNull(avaliacao);
            assertEquals(tipo, avaliacao.getTipoEntidade());
        }
    }

    @Test
    void shouldValidateTipoEntidadeEnumValues() {

        Avaliacao.TipoEntidade[] expectedTypes = {
            Avaliacao.TipoEntidade.PRODUTO,
            Avaliacao.TipoEntidade.SERVICO,
            Avaliacao.TipoEntidade.VETERINARIO,
            Avaliacao.TipoEntidade.LOJISTA
        };
        

        Avaliacao.TipoEntidade[] actualTypes = Avaliacao.TipoEntidade.values();
        

        assertEquals(expectedTypes.length, actualTypes.length);
        for (Avaliacao.TipoEntidade expectedType : expectedTypes) {
            assertTrue(java.util.Arrays.asList(actualTypes).contains(expectedType));
        }
    }

    @Test
    void shouldCreateAvaliacaoForEachTipoEntidade() {
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
        

        assertEquals(Avaliacao.TipoEntidade.PRODUTO, avaliacaoProduto.getTipoEntidade());
        assertEquals(Avaliacao.TipoEntidade.SERVICO, avaliacaoServico.getTipoEntidade());
        assertEquals(Avaliacao.TipoEntidade.VETERINARIO, avaliacaoVeterinario.getTipoEntidade());
        assertEquals(Avaliacao.TipoEntidade.LOJISTA, avaliacaoLojista.getTipoEntidade());
    }

    @Test
    void shouldHandleNullComment() {
        Avaliacao avaliacao = Avaliacao.builder()
                .nota(4)
                .comentario(null)
                .build();
        

        assertNotNull(avaliacao);
        assertNull(avaliacao.getComentario());
    }

    @Test
    void shouldHandleEmptyComment() {
        Avaliacao avaliacao = Avaliacao.builder()
                .nota(4)
                .comentario("")
                .build();
        

        assertNotNull(avaliacao);
        assertEquals("", avaliacao.getComentario());
    }

    @Test
    void shouldHandleLongComment() {

        String longComment = "Este é um comentário muito longo sobre o produto/serviço que foi avaliado. ".repeat(10);
        

        Avaliacao avaliacao = Avaliacao.builder()
                .nota(4)
                .comentario(longComment)
                .build();
        

        assertNotNull(avaliacao);
        assertEquals(longComment, avaliacao.getComentario());
    }

    @Test
    void shouldHandleSpecialCharactersInComment() {

        String commentWithSpecialChars = "Produto excelente! 5 estrelas ⭐⭐⭐⭐⭐ - Muito satisfeito 😊";
        

        Avaliacao avaliacao = Avaliacao.builder()
                .nota(5)
                .comentario(commentWithSpecialChars)
                .build();
        

        assertNotNull(avaliacao);
        assertEquals(commentWithSpecialChars, avaliacao.getComentario());
    }

    @Test
    void shouldToggleAtivoStatusMultipleTimes() {

        Avaliacao avaliacao = Avaliacao.builder()
                .ativo(true)
                .build();
        

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

        Avaliacao[] avaliacoes = {
            Avaliacao.builder().nota(1).build(),
            Avaliacao.builder().nota(2).build(),
            Avaliacao.builder().nota(3).build(),
            Avaliacao.builder().nota(4).build(),
            Avaliacao.builder().nota(5).build()
        };
        
        assertTrue(avaliacoes[0].isNotaRuim());
        assertFalse(avaliacoes[0].isNotaExcelente());
        
        assertTrue(avaliacoes[1].isNotaRuim());
        assertFalse(avaliacoes[1].isNotaExcelente());
        
        assertFalse(avaliacoes[2].isNotaRuim());
        assertFalse(avaliacoes[2].isNotaExcelente());
        
        assertFalse(avaliacoes[3].isNotaRuim());
        assertTrue(avaliacoes[3].isNotaExcelente());
        
        assertFalse(avaliacoes[4].isNotaRuim());
        assertTrue(avaliacoes[4].isNotaExcelente());
    }

    @Test
    void shouldCreateAvaliacaoWithMinimalData() {
        Avaliacao avaliacao = Avaliacao.builder()
                .entidadeId(UUID.randomUUID())
                .tipoEntidade(Avaliacao.TipoEntidade.PRODUTO)
                .autorId(UUID.randomUUID())
                .nota(3)
                .build();
        

        assertNotNull(avaliacao);
        assertNotNull(avaliacao.getEntidadeId());
        assertNotNull(avaliacao.getTipoEntidade());
        assertNotNull(avaliacao.getAutorId());
        assertEquals(3, avaliacao.getNota());
        assertDoesNotThrow(() -> avaliacao.validarNota());
    }

    @Test
    void shouldMaintainDataIntegrityAfterStatusChanges() {

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
        
        avaliacao.desativar();
        avaliacao.ativar();
        avaliacao.desativar();
        
        assertEquals(entidadeId, avaliacao.getEntidadeId());
        assertEquals(Avaliacao.TipoEntidade.SERVICO, avaliacao.getTipoEntidade());
        assertEquals(autorId, avaliacao.getAutorId());
        assertEquals(autorNome, avaliacao.getAutorNome());
        assertEquals(nota, avaliacao.getNota());
        assertEquals(comentario, avaliacao.getComentario());
        assertFalse(avaliacao.isAtivo()); 
    }
}
