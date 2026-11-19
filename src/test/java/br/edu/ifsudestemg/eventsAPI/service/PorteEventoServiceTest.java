package br.edu.ifsudestemg.eventsAPI.service;

import br.edu.ifsudestemg.eventsAPI.api.exeption.RegraNegocioException;
import br.edu.ifsudestemg.eventsAPI.model.entity.PorteEvento;
import br.edu.ifsudestemg.eventsAPI.model.repository.PorteEventoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.*;

class PorteEventoServiceTest {

    private PorteEventoRepository repository;
    private PorteEventoService service;
    private PorteEvento porteEvento;

    @BeforeEach
    void setUp() {
        repository = createMock(PorteEventoRepository.class);
        service = new PorteEventoService(repository);
        porteEvento = createNiceMock(PorteEvento.class);
    }


    @Test
    void deveRetornarListaDePorteEventos() {
        expect(repository.findAll()).andReturn(List.of(porteEvento));
        replay(repository, porteEvento);

        List<PorteEvento> resultado = service.getPorteEventos();

        assertEquals(1, resultado.size());
        verify(repository);
    }

    @Test
    void deveRetornarPorteEventoPorIdNoLimiarMinimoQuandoExistir() {
        expect(repository.findById(1L)).andReturn(Optional.of(porteEvento));
        replay(repository, porteEvento);

        Optional<PorteEvento> resultado = service.getPorteEventosById(1L);

        assertTrue(resultado.isPresent());
        verify(repository);
    }

    @Test
    void deveSalvarNovoPorteEventoNosLimiaresMinimosComSucesso() {
        expect(porteEvento.getId()).andStubReturn(null);
        expect(porteEvento.getNome()).andStubReturn("a");
        expect(porteEvento.getMinParticipantes()).andStubReturn(0);
        expect(porteEvento.getMaxParticipantes()).andStubReturn(0);

        expect(repository.existsByNomeIgnoreCase("a")).andReturn(false);
        expect(repository.save(porteEvento)).andReturn(porteEvento);

        replay(repository, porteEvento);

        PorteEvento salvo = service.salvar(porteEvento);

        assertSame(porteEvento, salvo);
        verify(repository);
    }

    @Test
    void deveSalvarEdicaoDePorteEventoExistenteSemValidarDuplicidadeDeNome() {
        expect(porteEvento.getId()).andStubReturn(1L);
        expect(porteEvento.getNome()).andStubReturn("Pequeno");
        expect(porteEvento.getMinParticipantes()).andStubReturn(1);
        expect(porteEvento.getMaxParticipantes()).andStubReturn(2);

        expect(repository.save(porteEvento)).andReturn(porteEvento);

        replay(repository, porteEvento);

        PorteEvento salvo = service.salvar(porteEvento);

        assertSame(porteEvento, salvo);
        verify(repository);
    }

    @Test
    void deveExcluirPorteEventoQuandoIdForValidoNoLimiarMinimo() {
        expect(porteEvento.getId()).andStubReturn(1L);
        repository.delete(porteEvento);
        expectLastCall().once();

        replay(repository, porteEvento);

        service.excluir(porteEvento);

        verify(repository);
    }

    @Test
    void deveLancarNullPointerExceptionAoExcluirPorteEventoComIdNulo() {
        expect(porteEvento.getId()).andStubReturn(null);
        replay(repository, porteEvento);

        assertThrows(NullPointerException.class, () -> service.excluir(porteEvento));
        verify(repository);
    }

    @Test
    void deveValidarComSucessoQuandoTodosOsAtributosOpcionaisForemNulos() {
        expect(porteEvento.getId()).andStubReturn(null);
        expect(porteEvento.getNome()).andStubReturn(null);
        expect(porteEvento.getMinParticipantes()).andStubReturn(null);
        expect(porteEvento.getMaxParticipantes()).andStubReturn(null);

        replay(repository, porteEvento);

        assertDoesNotThrow(() -> service.validar(porteEvento));
        verify(repository);
    }

    @Test
    void deveValidarComSucessoQuandoApenasMaxParticipantesForNuloEMinParticipantesNoLimiarZero() {
        expect(porteEvento.getId()).andStubReturn(1L);
        expect(porteEvento.getNome()).andStubReturn("Médio");
        expect(porteEvento.getMinParticipantes()).andStubReturn(0);
        expect(porteEvento.getMaxParticipantes()).andStubReturn(null);

        replay(repository, porteEvento);

        assertDoesNotThrow(() -> service.validar(porteEvento));
        verify(repository);
    }

    @Test
    void deveLancarExcecaoQuandoNomeForVazioOuApenasEspacos() {
        expect(porteEvento.getNome()).andStubReturn("   ");
        replay(repository, porteEvento);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> service.validar(porteEvento));
        assertEquals("Nome não pode ser vazio", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoNomeNaoContiverLetrasValidas() {
        expect(porteEvento.getNome()).andStubReturn("12345 - @#$");
        replay(repository, porteEvento);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> service.validar(porteEvento));
        assertEquals("Nome deve conter letras válidas", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoNovoPorteEventoJaExistirComMesmoNome() {
        expect(porteEvento.getId()).andStubReturn(null);
        expect(porteEvento.getNome()).andStubReturn("Grande");
        expect(repository.existsByNomeIgnoreCase("Grande")).andReturn(true);

        replay(repository, porteEvento);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> service.validar(porteEvento));
        assertEquals("Já existe um porte de evento com esse nome", ex.getMessage());
        verify(repository);
    }

    @Test
    void deveLancarExcecaoQuandoMinParticipantesForMaiorQueMaxParticipantesPorUm() {
        expect(porteEvento.getId()).andStubReturn(1L);
        expect(porteEvento.getNome()).andStubReturn("Grande");
        expect(porteEvento.getMinParticipantes()).andStubReturn(11);
        expect(porteEvento.getMaxParticipantes()).andStubReturn(10);

        replay(repository, porteEvento);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> service.validar(porteEvento));
        assertEquals(
                "O número mínimo de participantes não pode ser maior que o número máximo de participantes",
                ex.getMessage()
        );
    }

    @Test
    void deveLancarExcecaoQuandoMinParticipantesForMenorQueZeroNoLimiarMenosUm() {
        expect(porteEvento.getId()).andStubReturn(1L);
        expect(porteEvento.getNome()).andStubReturn("Pequeno");
        expect(porteEvento.getMinParticipantes()).andStubReturn(-1);
        expect(porteEvento.getMaxParticipantes()).andStubReturn(10);

        replay(repository, porteEvento);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> service.validar(porteEvento));
        assertEquals("O número mínimo de participantes não pode ser menor que 0", ex.getMessage());
    }
}