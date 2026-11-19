package br.edu.ifsudestemg.eventsAPI.api.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PagamentoIngressoDTOTest {

    @Test
    void deveArmazenarTipoIngressoEFormaPagamento() {
        PagamentoIngressoDTO dto = new PagamentoIngressoDTO();

        dto.setTipoIngresso("Inteira");
        dto.setFormaPagamento("PIX");

        assertEquals("Inteira", dto.getTipoIngresso());
        assertEquals("PIX", dto.getFormaPagamento());
    }

    @Test
    void deveManterAtributosNulosQuandoNaoInformados() {
        PagamentoIngressoDTO dto = new PagamentoIngressoDTO();

        assertNull(dto.getTipoIngresso());
        assertNull(dto.getFormaPagamento());
    }
}