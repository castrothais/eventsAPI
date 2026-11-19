package br.edu.ifsudestemg.eventsAPI.api.dto;

import br.edu.ifsudestemg.eventsAPI.model.entity.Atividade;
import br.edu.ifsudestemg.eventsAPI.model.entity.Evento;
import br.edu.ifsudestemg.eventsAPI.model.entity.TipoAtividade;
import br.edu.ifsudestemg.eventsAPI.model.entity.UsuarioPessoaFisica;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AtividadeDTOTest {

    @Test
    void deveCriarDTOComRelacionamentosNulos() {
        Atividade atividade = new Atividade();
        atividade.setId(1L);
        atividade.setNome("Palestra");
        atividade.setCancelado(false);

        AtividadeDTO dto = AtividadeDTO.create(atividade);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Palestra", dto.getNome());
        assertFalse(dto.isCancelado());

        assertNull(dto.getNomeEvento());
        assertNull(dto.getNomeMinistrante());
        assertNull(dto.getNomeTipoAtividade());
    }

    @Test
    void deveMapearNomeDoEventoQuandoRelacionamentoExistir() {
        Atividade atividade = new Atividade();
        atividade.setEvento(new Evento());
        atividade.getEvento().setNome("Evento de Tecnologia");

        AtividadeDTO dto = AtividadeDTO.create(atividade);

        assertEquals("Evento de Tecnologia", dto.getNomeEvento());
    }

    @Test
    void deveMapearNomeDoMinistranteQuandoRelacionamentoExistir() {
        Atividade atividade = new Atividade();

        UsuarioPessoaFisica ministrante = new UsuarioPessoaFisica();
        ministrante.setNome("Maria");

        atividade.setMinistrante(ministrante);

        AtividadeDTO dto = AtividadeDTO.create(atividade);

        assertEquals("Maria", dto.getNomeMinistrante());
    }

    @Test
    void deveMapearNomeDoTipoAtividadeQuandoRelacionamentoExistir() {
        Atividade atividade = new Atividade();

        TipoAtividade tipoAtividade = new TipoAtividade();
        tipoAtividade.setNome("Minicurso");

        atividade.setTipoAtividade(tipoAtividade);

        AtividadeDTO dto = AtividadeDTO.create(atividade);

        assertEquals("Minicurso", dto.getNomeTipoAtividade());
    }

    @Test
    void deveMapearTodosOsRelacionamentosQuandoExistirem() {
        Atividade atividade = new Atividade();

        Evento evento = new Evento();
        evento.setId(10L);
        evento.setNome("Semana Acadêmica");

        UsuarioPessoaFisica ministrante = new UsuarioPessoaFisica();
        ministrante.setId(20L);
        ministrante.setNome("João");

        TipoAtividade tipoAtividade = new TipoAtividade();
        tipoAtividade.setId(30L);
        tipoAtividade.setNome("Oficina");

        atividade.setId(1L);
        atividade.setNome("Oficina de Java");
        atividade.setCancelado(true);
        atividade.setEvento(evento);
        atividade.setMinistrante(ministrante);
        atividade.setTipoAtividade(tipoAtividade);

        AtividadeDTO dto = AtividadeDTO.create(atividade);

        assertEquals(1L, dto.getId());
        assertEquals("Oficina de Java", dto.getNome());
        assertTrue(dto.isCancelado());

        assertEquals(10L, dto.getIdEvento());
        assertEquals("Semana Acadêmica", dto.getNomeEvento());

        assertEquals(20L, dto.getIdMinistrante());
        assertEquals("João", dto.getNomeMinistrante());

        assertEquals(30L, dto.getIdTipoAtividade());
        assertEquals("Oficina", dto.getNomeTipoAtividade());
    }

}