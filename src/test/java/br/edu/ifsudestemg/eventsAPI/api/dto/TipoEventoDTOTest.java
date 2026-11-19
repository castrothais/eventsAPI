package br.edu.ifsudestemg.eventsAPI.api.dto;

import br.edu.ifsudestemg.eventsAPI.model.entity.TipoEvento;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TipoEventoDTOTest {

    @Test
    void deveCriarDTOComTodosOsAtributosPreenchidos() {
        TipoEvento tipoEvento = new TipoEvento();

        tipoEvento.setId(1L);
        tipoEvento.setNome("Acadêmico");
        tipoEvento.setDescricao("Eventos relacionados ao meio acadêmico");

        TipoEventoDTO dto = TipoEventoDTO.create(tipoEvento);

        assertNotNull(dto);

        assertEquals(1L, dto.getId());
        assertEquals("Acadêmico", dto.getNome());
        assertEquals("Eventos relacionados ao meio acadêmico", dto.getDescricao());
    }

    @Test
    void deveCriarDTOComAtributosNulos() {
        TipoEvento tipoEvento = new TipoEvento();

        TipoEventoDTO dto = TipoEventoDTO.create(tipoEvento);

        assertNotNull(dto);

        assertNull(dto.getId());
        assertNull(dto.getNome());
        assertNull(dto.getDescricao());
    }

}