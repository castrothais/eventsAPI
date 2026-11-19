package br.edu.ifsudestemg.eventsAPI.api.dto;

import br.edu.ifsudestemg.eventsAPI.model.entity.TipoAtividade;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TipoAtividadeDTOTest {

    @Test
    void deveCriarDTOComTodosOsAtributosPreenchidos() {
        TipoAtividade tipoAtividade = new TipoAtividade();

        tipoAtividade.setId(1L);
        tipoAtividade.setNome("Palestra");

        TipoAtividadeDTO dto = TipoAtividadeDTO.create(tipoAtividade);

        assertNotNull(dto);

        assertEquals(1L, dto.getId());
        assertEquals("Palestra", dto.getNome());
    }

    @Test
    void deveCriarDTOComAtributosNulos() {
        TipoAtividade tipoAtividade = new TipoAtividade();

        TipoAtividadeDTO dto = TipoAtividadeDTO.create(tipoAtividade);

        assertNotNull(dto);

        assertNull(dto.getId());
        assertNull(dto.getNome());
    }
}