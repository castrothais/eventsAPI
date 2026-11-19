package br.edu.ifsudestemg.eventsAPI.api.dto;

import br.edu.ifsudestemg.eventsAPI.model.entity.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EventoDTOTest {

    @Test
    void deveCriarDTOComTodosOsAtributosPreenchidos() {
        Evento evento = new Evento();

        UsuarioPessoaFisica organizador = new UsuarioPessoaFisica();
        organizador.setNome("Maria");

        TipoEvento tipoEvento = new TipoEvento();
        tipoEvento.setId(10L);
        tipoEvento.setNome("Acadêmico");

        PorteEvento porteEvento = new PorteEvento();
        porteEvento.setId(20L);
        porteEvento.setNome("Grande");

        Endereco endereco = new Endereco();
        endereco.setCidade("Juiz de Fora");

        evento.setId(1L);
        evento.setNome("Semana Acadêmica");
        evento.setImagem("evento.jpg");
        evento.setModalidade(Modalidade.PRESENCIAL);
        evento.setDataHoraInicio(LocalDateTime.of(2027, 5, 10, 8, 0));
        evento.setDataHoraFim(LocalDateTime.of(2027, 5, 12, 18, 0));
        evento.setDescricao("Evento de tecnologia");
        evento.setIdadeMinima(16);
        evento.setLotacaoMaxima(500);
        evento.setCancelado(false);
        evento.setEndereco(endereco);
        evento.setValorIngresso(new BigDecimal("50.00"));
        evento.setOrganizador(organizador);
        evento.setTipoEvento(tipoEvento);
        evento.setPorteEvento(porteEvento);

        EventoDTO dto = EventoDTO.create(evento);

        assertNotNull(dto);

        assertEquals(1L, dto.getId());
        assertEquals("Semana Acadêmica", dto.getNome());
        assertEquals("evento.jpg", dto.getImagem());
        assertEquals("PRESENCIAL", dto.getModalidade());
        assertEquals(16, dto.getIdadeMinima());
        assertEquals(500, dto.getLotacaoMaxima());
        assertFalse(dto.isCancelado());
        assertEquals(new BigDecimal("50.00"), dto.getValorIngresso());

        assertEquals("Juiz de Fora", dto.getEndereco().getCidade());

        assertEquals("Maria", dto.getNomeOrganizador());
        assertEquals(10L, dto.getIdTipoEvento());
        assertEquals("Acadêmico", dto.getNomeTipoEvento());
        assertEquals(20L, dto.getIdPorteEvento());
        assertEquals("Grande", dto.getNomePorteEvento());
    }

    @Test
    void deveCriarDTOComAtributosNulos() {
        Evento evento = new Evento();

        UsuarioPessoaFisica organizador = new UsuarioPessoaFisica();
        organizador.setNome("Maria");

        TipoEvento tipoEvento = new TipoEvento();
        tipoEvento.setId(10L);
        tipoEvento.setNome("Acadêmico");

        PorteEvento porteEvento = new PorteEvento();
        porteEvento.setId(20L);
        porteEvento.setNome("Grande");

        evento.setOrganizador(organizador);
        evento.setTipoEvento(tipoEvento);
        evento.setPorteEvento(porteEvento);

        EventoDTO dto = EventoDTO.create(evento);

        assertNotNull(dto);

        assertNull(dto.getId());
        assertNull(dto.getNome());
        assertNull(dto.getImagem());
        assertNull(dto.getModalidade());
        assertNull(dto.getDataHoraInicio());
        assertNull(dto.getDataHoraFim());
        assertNull(dto.getDescricao());
        assertNull(dto.getIdadeMinima());
        assertNull(dto.getLotacaoMaxima());
        assertNull(dto.getEndereco());
        assertNull(dto.getValorIngresso());

        assertFalse(dto.isCancelado());

        assertEquals("Maria", dto.getNomeOrganizador());
        assertEquals(10L, dto.getIdTipoEvento());
        assertEquals("Acadêmico", dto.getNomeTipoEvento());
        assertEquals(20L, dto.getIdPorteEvento());
        assertEquals("Grande", dto.getNomePorteEvento());
    }
}