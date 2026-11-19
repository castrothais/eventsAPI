package br.edu.ifsudestemg.eventsAPI.api.dto;

import br.edu.ifsudestemg.eventsAPI.model.entity.PorteEvento;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PorteEventoDTOTest {

    @Test
    void deveCriarDTOComTodosOsAtributosPreenchidos() {
        PorteEvento porteEvento = new PorteEvento();

        porteEvento.setId(1L);
        porteEvento.setNome("Grande");
        porteEvento.setMinParticipantes(100);
        porteEvento.setMaxParticipantes(500);
        porteEvento.setTempoMinimoCancelamento(1440);
        porteEvento.setDescricao("Eventos de grande porte");

        PorteEventoDTO dto = PorteEventoDTO.create(porteEvento);

        assertNotNull(dto);

        assertEquals(1L, dto.getId());
        assertEquals("Grande", dto.getNome());
        assertEquals(100, dto.getMinParticipantes());
        assertEquals(500, dto.getMaxParticipantes());
        assertEquals(1440, dto.getTempoMinimoCancelamento());
        assertEquals("Eventos de grande porte", dto.getDescricao());
    }

    @Test
    void deveCriarDTOComAtributosNulos() {
        PorteEvento porteEvento = new PorteEvento();

        PorteEventoDTO dto = PorteEventoDTO.create(porteEvento);

        assertNotNull(dto);

        assertNull(dto.getId());
        assertNull(dto.getNome());
        assertNull(dto.getMinParticipantes());
        assertNull(dto.getMaxParticipantes());
        assertNull(dto.getTempoMinimoCancelamento());
        assertNull(dto.getDescricao());
    }
}
