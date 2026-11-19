package br.edu.ifsudestemg.eventsAPI.api.dto;

import br.edu.ifsudestemg.eventsAPI.model.entity.Atividade;
import br.edu.ifsudestemg.eventsAPI.model.entity.Presenca;
import br.edu.ifsudestemg.eventsAPI.model.entity.UsuarioPessoaFisica;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PresencaDTOTest {

    @Test
    void deveCriarDTOComTodosOsAtributosPreenchidos() {
        Presenca presenca = new Presenca();

        UsuarioPessoaFisica participante = new UsuarioPessoaFisica();
        participante.setNome("João");

        Atividade atividade = new Atividade();
        atividade.setId(10L);
        atividade.setNome("Palestra sobre Tecnologia");

        presenca.setId(1L);
        presenca.setPresente(true);
        presenca.setParticipante(participante);
        presenca.setAtividade(atividade);

        PresencaDTO dto = PresencaDTO.create(presenca);

        assertNotNull(dto);

        assertEquals(1L, dto.getId());
        assertTrue(dto.getPresente());

        assertEquals("João", dto.getNomeParticipante());
        assertEquals(10L, dto.getIdAtividade());
        assertEquals("Palestra sobre Tecnologia", dto.getNomeAtividade());
    }

    @Test
    void deveCriarDTOComAtributosNulos() {
        Presenca presenca = new Presenca();

        UsuarioPessoaFisica participante = new UsuarioPessoaFisica();
        participante.setNome("João");

        Atividade atividade = new Atividade();
        atividade.setNome("Palestra sobre Tecnologia");

        presenca.setParticipante(participante);
        presenca.setAtividade(atividade);

        PresencaDTO dto = PresencaDTO.create(presenca);

        assertNotNull(dto);

        assertNull(dto.getId());
        assertFalse(dto.getPresente());

        assertEquals("João", dto.getNomeParticipante());
        assertNull(dto.getIdAtividade());
        assertEquals("Palestra sobre Tecnologia", dto.getNomeAtividade());
    }
}