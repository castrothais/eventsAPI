package br.edu.ifsudestemg.eventsAPI.api.dto;

import br.edu.ifsudestemg.eventsAPI.model.entity.Modalidade;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ModalidadeDTOTest {

    @Test
    void deveCriarDTOComModalidadeOnline() {
        ModalidadeDTO dto = ModalidadeDTO.create(Modalidade.ONLINE);

        assertNotNull(dto);
        assertEquals("ONLINE", dto.getModalidade());
    }

    @Test
    void deveCriarDTOComModalidadePresencial() {
        ModalidadeDTO dto = ModalidadeDTO.create(Modalidade.PRESENCIAL);

        assertNotNull(dto);
        assertEquals("PRESENCIAL", dto.getModalidade());
    }

    @Test
    void deveCriarDTOComModalidadeHibrida() {
        ModalidadeDTO dto = ModalidadeDTO.create(Modalidade.HIBRIDO);

        assertNotNull(dto);
        assertEquals("HIBRIDO", dto.getModalidade());
    }

    @Test
    void deveLancarExcecaoQuandoModalidadeForNula() {
        assertThrows(IllegalArgumentException.class, () -> ModalidadeDTO.create(null));
    }

}