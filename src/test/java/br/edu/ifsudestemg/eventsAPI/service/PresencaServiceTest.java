package br.edu.ifsudestemg.eventsAPI.service;

import br.edu.ifsudestemg.eventsAPI.api.exeption.RegraNegocioException;
import br.edu.ifsudestemg.eventsAPI.model.entity.Atividade;
import br.edu.ifsudestemg.eventsAPI.model.entity.Evento;
import br.edu.ifsudestemg.eventsAPI.model.entity.Presenca;
import br.edu.ifsudestemg.eventsAPI.model.repository.PresencaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.*;

class PresencaServiceTest {

    private PresencaRepository repository;
    private PresencaService service;
    private Presenca presenca;
    private Atividade atividade;
    private Evento evento;

    @BeforeEach
    void setUp() {
        repository = createMock(PresencaRepository.class);
        service = new PresencaService(repository);

        presenca = createNiceMock(Presenca.class);
        atividade = createNiceMock(Atividade.class);
        evento = createNiceMock(Evento.class);
    }

    @Test
    void deveRetornarListaDePresencas() {
        expect(repository.findAll()).andReturn(List.of(presenca));
        replay(repository, presenca, atividade, evento);

        List<Presenca> resultado = service.getPresencas();

        assertEquals(1, resultado.size());
        verify(repository);
    }

    @Test
    void deveRetornarPresencaPorIdNoLimiarMinimoQuandoExistir() {
        expect(repository.findById(1L)).andReturn(Optional.of(presenca));
        replay(repository, presenca, atividade, evento);

        Optional<Presenca> resultado = service.getPresencasById(1L);

        assertTrue(resultado.isPresent());
        verify(repository);
    }

    @Test
    void deveSalvarPresencaNosLimiaresDeHorarioDoEventoComSucesso() {
        LocalDateTime agora = LocalDateTime.now();

        expect(presenca.getAtividade()).andStubReturn(atividade);
        expect(atividade.getEvento()).andStubReturn(evento);
        expect(evento.getDataHoraInicio()).andStubReturn(agora.minusSeconds(1));
        expect(evento.getDataHoraFim()).andStubReturn(agora.plusSeconds(1));

        expect(repository.save(presenca)).andReturn(presenca);

        replay(repository, presenca, atividade, evento);

        Presenca salva = service.salvar(presenca);

        assertSame(presenca, salva);
        verify(repository);
    }

    @Test
    void deveExcluirPresencaQuandoIdForValidoNoLimiarMinimo() {
        expect(presenca.getId()).andStubReturn(1L);
        repository.delete(presenca);
        expectLastCall().once();

        replay(repository, presenca, atividade, evento);

        service.excluir(presenca);

        verify(repository);
    }

    @Test
    void deveLancarNullPointerExceptionAoExcluirPresencaComIdNulo() {
        expect(presenca.getId()).andStubReturn(null);
        replay(repository, presenca, atividade, evento);

        assertThrows(NullPointerException.class, () -> service.excluir(presenca));
        verify(repository);
    }


    @Test
    void deveLancarExcecaoQuandoEventoAindaNaoTiverIniciadoPorUmSegundo() {
        LocalDateTime agora = LocalDateTime.now();

        expect(presenca.getAtividade()).andStubReturn(atividade);
        expect(atividade.getEvento()).andStubReturn(evento);
        expect(evento.getDataHoraInicio()).andStubReturn(agora.plusSeconds(1));

        replay(repository, presenca, atividade, evento);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> service.validar(presenca));
        assertEquals("A presença não pode ser alterada fora da realização do evento", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoEventoJaTiverEncerradoPorUmSegundo() {
        LocalDateTime agora = LocalDateTime.now();

        expect(presenca.getAtividade()).andStubReturn(atividade);
        expect(atividade.getEvento()).andStubReturn(evento);
        expect(evento.getDataHoraInicio()).andStubReturn(agora.minusHours(1));
        expect(evento.getDataHoraFim()).andStubReturn(agora.minusSeconds(1));

        replay(repository, presenca, atividade, evento);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> service.validar(presenca));
        assertEquals("A presença não pode ser alterada fora da realização do evento", ex.getMessage());
    }
}