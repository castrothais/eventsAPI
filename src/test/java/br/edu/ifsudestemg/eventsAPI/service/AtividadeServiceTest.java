package br.edu.ifsudestemg.eventsAPI.service;

import br.edu.ifsudestemg.eventsAPI.api.exeption.RegraNegocioException;
import br.edu.ifsudestemg.eventsAPI.model.entity.Atividade;
import br.edu.ifsudestemg.eventsAPI.model.entity.Evento;
import br.edu.ifsudestemg.eventsAPI.model.entity.Perfil;
import br.edu.ifsudestemg.eventsAPI.model.entity.Usuario;
import br.edu.ifsudestemg.eventsAPI.model.repository.AtividadeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.*;

class AtividadeServiceTest {

    private AtividadeRepository repository;
    private AtividadeService service;
    private Atividade atividade;
    private Evento evento;
    private Usuario ministrante;

    @BeforeEach
    void setUp() {
        repository = createMock(AtividadeRepository.class);
        service = new AtividadeService(repository);

        atividade = createNiceMock(Atividade.class);
        evento = createNiceMock(Evento.class);
        ministrante = createNiceMock(Usuario.class);
    }


    @Test
    void deveRetornarLista() {
        expect(repository.findAll()).andReturn(List.of(atividade));
        replay(repository, atividade, evento, ministrante);

        List<Atividade> resultado = service.getAtividades();

        assertEquals(1, resultado.size());
        verify(repository);
    }

    @Test

    void deveRetornarOptionalPreenchido() {
        expect(repository.findById(1L)).andReturn(Optional.of(atividade));
        replay(repository, atividade, evento, ministrante);

        Optional<Atividade> resultado = service.getAtividadesById(1L);

        assertTrue(resultado.isPresent());
        verify(repository);
    }

    @Test
    void deveSalvarComSucessoNovaAtividadeValida() {
        expect(atividade.getId()).andStubReturn(null);
        expect(atividade.getNome()).andStubReturn("Teste");
        expect(atividade.getEvento()).andStubReturn(evento);
        expect(atividade.getMinistrante()).andStubReturn(ministrante);
        expect(atividade.isCancelado()).andStubReturn(false);
        expect(ministrante.getPerfis()).andStubReturn(List.of(Perfil.MEDIADOR));
        expect(evento.getId()).andStubReturn(1L);
        expect(evento.getDataHoraFim()).andStubReturn(LocalDateTime.now().plusDays(1));

        expect(repository.existsByNomeIgnoreCaseAndEventoId("Teste", 1L)).andReturn(false);
        expect(repository.save(atividade)).andReturn(atividade);

        replay(repository, atividade, evento, ministrante);

        Atividade salva = service.salvar(atividade);

        assertNotNull(salva);
        verify(repository);
    }

    @Test
    void deveSalvarComSucessoAtividadeExistenteValida() {
        expect(atividade.getId()).andStubReturn(1L);
        expect(atividade.getNome()).andStubReturn("Teste");
        expect(atividade.getEvento()).andStubReturn(evento);
        expect(atividade.getMinistrante()).andStubReturn(ministrante);
        expect(atividade.isCancelado()).andStubReturn(false);
        expect(ministrante.getPerfis()).andStubReturn(List.of(Perfil.MEDIADOR));
        expect(evento.getDataHoraFim()).andStubReturn(LocalDateTime.now().plusDays(1));

        expect(repository.save(atividade)).andReturn(atividade);

        replay(repository, atividade, evento, ministrante);

        Atividade salva = service.salvar(atividade);

        assertNotNull(salva);
        verify(repository);
    }

    @Test
    void deveChamarExcluirAtividadeValida() {
        expect(atividade.getId()).andStubReturn(1L);
        repository.delete(atividade);
        expectLastCall().once();

        replay(repository, atividade, evento, ministrante);

        service.excluir(atividade);

        verify(repository);
    }

    @Test
    void deveLancarExcecaoParaIdNulo() {
        expect(atividade.getId()).andStubReturn(null);

        replay(repository, atividade, evento, ministrante);

        assertThrows(NullPointerException.class, () -> service.excluir(atividade));
        verify(repository);
    }


    @Test
    void deveLancarExcecaoParaNomeVazio() {
        expect(atividade.getNome()).andStubReturn("   ");
        replay(repository, atividade, evento, ministrante);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> service.validar(atividade));
        assertEquals("Nome não pode ser vazio", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoParaNomeSemLetras() {
        expect(atividade.getNome()).andStubReturn("12345 - @#$");
        replay(repository, atividade, evento, ministrante);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> service.validar(atividade));
        assertEquals("Nome deve conter letras válidas", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoParaEventosNulos() {
        expect(atividade.getNome()).andStubReturn("Teste");
        expect(atividade.getEvento()).andStubReturn(null);
        replay(repository, atividade, evento, ministrante);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> service.validar(atividade));
        assertEquals("Evento é obrigatório", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoParaMinistranteNulo() {
        expect(atividade.getNome()).andStubReturn("Teste");
        expect(atividade.getEvento()).andStubReturn(evento);
        expect(atividade.getMinistrante()).andStubReturn(null);
        replay(repository, atividade, evento, ministrante);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> service.validar(atividade));
        assertEquals("Ministrante é obrigatório", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoParaPerfilMinistranteNulo() {
        expect(atividade.getNome()).andStubReturn("Teste");
        expect(atividade.getEvento()).andStubReturn(evento);
        expect(atividade.getMinistrante()).andStubReturn(ministrante);
        expect(ministrante.getPerfis()).andStubReturn(null);
        replay(repository, atividade, evento, ministrante);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> service.validar(atividade));
        assertEquals("Usuário não possui perfil MEDIADOR", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoParaMinistranteSemPerfilMediador() {
        expect(atividade.getNome()).andStubReturn("Teste");
        expect(atividade.getEvento()).andStubReturn(evento);
        expect(atividade.getMinistrante()).andStubReturn(ministrante);
        expect(ministrante.getPerfis()).andStubReturn(Collections.emptyList());
        replay(repository, atividade, evento, ministrante);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> service.validar(atividade));
        assertEquals("Usuário não possui perfil MEDIADOR", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoParaAtividadeDuplicada() {
        expect(atividade.getId()).andStubReturn(null);
        expect(atividade.getNome()).andStubReturn("Teste");
        expect(atividade.getEvento()).andStubReturn(evento);
        expect(atividade.getMinistrante()).andStubReturn(ministrante);
        expect(ministrante.getPerfis()).andStubReturn(List.of(Perfil.MEDIADOR));
        expect(evento.getId()).andStubReturn(1L);

        expect(repository.existsByNomeIgnoreCaseAndEventoId("Teste", 1L)).andReturn(true);

        replay(repository, atividade, evento, ministrante);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> service.validar(atividade));
        assertEquals("Já existe uma atividade com esse nome", ex.getMessage());
        verify(repository);
    }

    @Test
    void deveLancarExcecaoParaDataHoraFimNulo() {
        expect(atividade.getId()).andStubReturn(1L);
        expect(atividade.getNome()).andStubReturn("Teste");
        expect(atividade.getEvento()).andStubReturn(evento);
        expect(atividade.getMinistrante()).andStubReturn(ministrante);
        expect(ministrante.getPerfis()).andStubReturn(List.of(Perfil.MEDIADOR));
        expect(evento.getDataHoraFim()).andStubReturn(null);

        replay(repository, atividade, evento, ministrante);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> service.validar(atividade));
        assertEquals("Evento sem data de término definida", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoParaEventoFinalizado() {
        expect(atividade.getId()).andStubReturn(1L);
        expect(atividade.getNome()).andStubReturn("Teste");
        expect(atividade.getEvento()).andStubReturn(evento);
        expect(atividade.getMinistrante()).andStubReturn(ministrante);
        expect(ministrante.getPerfis()).andStubReturn(List.of(Perfil.MEDIADOR));
        expect(evento.getDataHoraFim()).andStubReturn(LocalDateTime.now().minusMinutes(1));

        replay(repository, atividade, evento, ministrante);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> service.validar(atividade));
        assertEquals("Criação/edição de atividade não permitida, evento principal finalizado", ex.getMessage());
    }

    @Test
    void devevalidarCancelamentoDeEventoAtivoComMaisDeUmaAtividade() {
        expect(atividade.getId()).andStubReturn(2L);
        expect(atividade.getNome()).andStubReturn("Teste");
        expect(atividade.getEvento()).andStubReturn(evento);
        expect(atividade.getMinistrante()).andStubReturn(ministrante);
        expect(atividade.isCancelado()).andStubReturn(true);
        expect(ministrante.getPerfis()).andStubReturn(List.of(Perfil.MEDIADOR));
        expect(evento.getId()).andStubReturn(1L);
        expect(evento.getDataHoraFim()).andStubReturn(LocalDateTime.now().plusDays(1));
        expect(evento.isCancelado()).andStubReturn(false);

        expect(repository.findById(2L)).andReturn(Optional.of(atividade));
        expect(repository.countByEventoId(1L)).andReturn(2);

        replay(repository, atividade, evento, ministrante);

        assertDoesNotThrow(() -> service.validar(atividade));
        verify(repository);
    }

    @Test
    void deveValidarCancelamentoDeEventoJaCancelado() {
        expect(atividade.getId()).andStubReturn(2L);
        expect(atividade.getNome()).andStubReturn("Teste");
        expect(atividade.getEvento()).andStubReturn(evento);
        expect(atividade.getMinistrante()).andStubReturn(ministrante);
        expect(atividade.isCancelado()).andStubReturn(true);
        expect(ministrante.getPerfis()).andStubReturn(List.of(Perfil.MEDIADOR));
        expect(evento.getId()).andStubReturn(1L);
        expect(evento.getDataHoraFim()).andStubReturn(LocalDateTime.now().plusDays(1));
        expect(evento.isCancelado()).andStubReturn(true);

        expect(repository.findById(2L)).andReturn(Optional.of(atividade));
        expect(repository.countByEventoId(1L)).andReturn(1);

        replay(repository, atividade, evento, ministrante);

        assertDoesNotThrow(() -> service.validar(atividade));
        verify(repository);
    }

    @Test
    void deveLancarExcecaoAoCancelarAtividadeNaoEncontrada() {
        expect(atividade.getId()).andStubReturn(3L);
        expect(atividade.getNome()).andStubReturn("Teste");
        expect(atividade.getEvento()).andStubReturn(evento);
        expect(atividade.getMinistrante()).andStubReturn(ministrante);
        expect(atividade.isCancelado()).andStubReturn(true);
        expect(ministrante.getPerfis()).andStubReturn(List.of(Perfil.MEDIADOR));
        expect(evento.getDataHoraFim()).andStubReturn(LocalDateTime.now().plusDays(1));

        expect(repository.findById(3L)).andReturn(Optional.empty());

        replay(repository, atividade, evento, ministrante);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> service.validar(atividade));
        assertEquals("Atividade não encontrada", ex.getMessage());
        verify(repository);
    }

    @Test
    void deveLancarExcecaoAoCancelarAtividadeComIdNulo() {
        expect(atividade.getId()).andStubReturn(null);
        expect(repository.findById(null)).andReturn(Optional.empty());

        replay(repository, atividade, evento, ministrante);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> service.validarCancelamentoAtividade(atividade));
        assertEquals("Atividade não encontrada", ex.getMessage());
        verify(repository);
    }

    @Test
    void deveLancarExcecaoAoCancelarUnicaAtividadeDeEventoAtivo() {
        expect(atividade.getId()).andStubReturn(2L);
        expect(atividade.getNome()).andStubReturn("Teste");
        expect(atividade.getEvento()).andStubReturn(evento);
        expect(atividade.getMinistrante()).andStubReturn(ministrante);
        expect(atividade.isCancelado()).andStubReturn(true);
        expect(ministrante.getPerfis()).andStubReturn(List.of(Perfil.MEDIADOR));
        expect(evento.getId()).andStubReturn(1L);
        expect(evento.getDataHoraFim()).andStubReturn(LocalDateTime.now().plusDays(1));
        expect(evento.isCancelado()).andStubReturn(false);

        expect(repository.findById(2L)).andReturn(Optional.of(atividade));
        expect(repository.countByEventoId(1L)).andReturn(1);

        replay(repository, atividade, evento, ministrante);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> service.validar(atividade));
        assertEquals("Evento ativo deve possuir ao menos uma atividade", ex.getMessage());
        verify(repository);
    }

    @Test
    void deveLancarExcecaoQuandoIdForNuloNoCancelamento() {
        expect(atividade.getId()).andReturn(1L);
        expect(atividade.getId()).andReturn(null);
        expect(repository.findById(1L)).andReturn(Optional.of(atividade));

        replay(repository, atividade, evento, ministrante);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> service.validarCancelamentoAtividade(atividade));
        assertEquals("Atividade não encontrada", ex.getMessage());
        verify(repository, atividade);
    }

    @Test
    void deveRetornarInstanciaNaoNulaDeModelMapper() {
        assertNotNull(service.modelMapper());
    }

}