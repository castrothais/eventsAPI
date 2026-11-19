package br.edu.ifsudestemg.eventsAPI.api.dto;

import br.edu.ifsudestemg.eventsAPI.model.entity.Ingresso;
import br.edu.ifsudestemg.eventsAPI.model.entity.MovimentacaoCarteira;
import br.edu.ifsudestemg.eventsAPI.model.entity.TipoMovimentacao;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class MovimentacaoCarteiraDTOTest {

    @Test
    void deveCriarDTOComTodosOsAtributosPreenchidos() {
        MovimentacaoCarteira movimentacao = new MovimentacaoCarteira();

        Ingresso ingresso = new Ingresso();
        ingresso.setId(10L);

        LocalDateTime dataHora = LocalDateTime.of(2026, 11, 14, 10, 30);

        movimentacao.setId(1L);
        movimentacao.setTipo(TipoMovimentacao.CREDITO);
        movimentacao.setValor(new BigDecimal("100.00"));
        movimentacao.setDataHora(dataHora);
        movimentacao.setIngresso(ingresso);

        MovimentacaoCarteiraDTO dto = MovimentacaoCarteiraDTO.create(movimentacao);

        assertNotNull(dto);

        assertEquals(1L, dto.getId());
        assertEquals(TipoMovimentacao.CREDITO, dto.getTipo());
        assertEquals(new BigDecimal("100.00"), dto.getValor());
        assertEquals(dataHora, dto.getDataHora());
        assertEquals(10L, dto.getIdIngresso());
    }

    @Test
    void deveCriarDTOSemIngressoAssociado() {
        MovimentacaoCarteira movimentacao = new MovimentacaoCarteira();

        movimentacao.setId(2L);
        movimentacao.setTipo(TipoMovimentacao.DEBITO);
        movimentacao.setValor(new BigDecimal("50.00"));
        movimentacao.setDataHora(LocalDateTime.of(2026, 11, 14, 11, 0));
        movimentacao.setIngresso(null);

        MovimentacaoCarteiraDTO dto = MovimentacaoCarteiraDTO.create(movimentacao);

        assertNotNull(dto);

        assertEquals(2L, dto.getId());
        assertEquals(TipoMovimentacao.DEBITO, dto.getTipo());
        assertEquals(new BigDecimal("50.00"), dto.getValor());
        assertNotNull(dto.getDataHora());
        assertNull(dto.getIdIngresso());
    }

    @Test
    void deveCriarDTOComAtributosNulos() {
        MovimentacaoCarteira movimentacao = new MovimentacaoCarteira();

        MovimentacaoCarteiraDTO dto = MovimentacaoCarteiraDTO.create(movimentacao);

        assertNotNull(dto);

        assertNull(dto.getId());
        assertNull(dto.getTipo());
        assertNull(dto.getValor());
        assertNull(dto.getDataHora());
        assertNull(dto.getIdIngresso());
    }
}