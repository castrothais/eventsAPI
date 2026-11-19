package br.edu.ifsudestemg.eventsAPI.api.dto;

import br.edu.ifsudestemg.eventsAPI.model.entity.Evento;
import br.edu.ifsudestemg.eventsAPI.model.entity.Ingresso;
import br.edu.ifsudestemg.eventsAPI.model.entity.StatusIngresso;
import br.edu.ifsudestemg.eventsAPI.model.entity.UsuarioPessoaFisica;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class IngressoDTOTest {

    @Test
    void deveCriarDTOComTodosOsAtributosPreenchidos() {
        Ingresso ingresso = new Ingresso();

        UsuarioPessoaFisica participante = new UsuarioPessoaFisica();
        participante.setNome("João");

        Evento evento = new Evento();
        evento.setNome("Semana Acadêmica");

        ingresso.setId(1L);
        ingresso.setStatus(StatusIngresso.PAGO);
        ingresso.setValor(new BigDecimal("100.00"));
        ingresso.setTaxa(new BigDecimal("5.00"));
        ingresso.setTipoIngresso("INTEIRA");
        ingresso.setParticipante(participante);
        ingresso.setEvento(evento);

        IngressoDTO dto = IngressoDTO.create(ingresso);

        assertNotNull(dto);

        assertEquals(1L, dto.getId());
        assertEquals("PAGO", dto.getStatus());
        assertEquals(new BigDecimal("100.00"), dto.getValor());
        assertEquals(new BigDecimal("5.00"), dto.getTaxa());
        assertEquals("INTEIRA", dto.getTipoIngresso());

        assertEquals("João", dto.getNomeParticipante());
        assertEquals("Semana Acadêmica", dto.getNomeEvento());
    }

    @Test
    void deveCriarDTOComAtributosNulos() {
        Ingresso ingresso = new Ingresso();

        UsuarioPessoaFisica participante = new UsuarioPessoaFisica();
        participante.setNome("João");

        Evento evento = new Evento();
        evento.setNome("Semana Acadêmica");

        ingresso.setParticipante(participante);
        ingresso.setEvento(evento);

        IngressoDTO dto = IngressoDTO.create(ingresso);

        assertNotNull(dto);

        assertNull(dto.getId());
        assertNull(dto.getStatus());
        assertNull(dto.getValor());
        assertNull(dto.getTaxa());
        assertNull(dto.getTipoIngresso());

        assertEquals("João", dto.getNomeParticipante());
        assertEquals("Semana Acadêmica", dto.getNomeEvento());
    }

}