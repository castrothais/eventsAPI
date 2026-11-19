package br.edu.ifsudestemg.eventsAPI.service;

import br.edu.ifsudestemg.eventsAPI.api.exeption.RegraNegocioException;
import br.edu.ifsudestemg.eventsAPI.model.entity.TipoEvento;
import br.edu.ifsudestemg.eventsAPI.model.repository.TipoEventoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.*;

class TipoEventoServiceTest {

    private TipoEventoRepository repository;
    private TipoEventoService service;
    private TipoEvento tipoEvento;

    @BeforeEach
    void setUp() {
        repository = createMock(TipoEventoRepository.class);
        service = new TipoEventoService(repository);
        tipoEvento = createNiceMock(TipoEvento.class);
    }

    @Test
    void deveRetornarListaDeTipoEventos() {
        expect(repository.findAll()).andReturn(List.of(tipoEvento));
        replay(repository, tipoEvento);

        List<TipoEvento> resultado = service.getTipoEventos();

        assertEquals(1, resultado.size());
        verify(repository);
    }

    @Test
    void deveRetornarTipoEventoPorIdNoLimiarMinimoQuandoExistir() {
        expect(repository.findById(1L)).andReturn(Optional.of(tipoEvento));
        replay(repository, tipoEvento);

        Optional<TipoEvento> resultado = service.getTipoEventosById(1L);

        assertTrue(resultado.isPresent());
        verify(repository);
    }

    @Test
    void deveSalvarNovoTipoEventoNoLimiarMinimoDeCaracteresComSucesso() {
        expect(tipoEvento.getId()).andStubReturn(null);
        expect(tipoEvento.getNome()).andStubReturn("a");

        expect(repository.existsByNomeIgnoreCase("a")).andReturn(false);
        expect(repository.save(tipoEvento)).andReturn(tipoEvento);

        replay(repository, tipoEvento);

        TipoEvento salvo = service.salvar(tipoEvento);

        assertSame(tipoEvento, salvo);
        verify(repository);
    }

    @Test
    void deveSalvarEdicaoDeTipoEventoExistenteSemValidarDuplicidadeDeNome() {
        expect(tipoEvento.getId()).andStubReturn(1L);
        expect(tipoEvento.getNome()).andStubReturn("Teste");

        expect(repository.save(tipoEvento)).andReturn(tipoEvento);

        replay(repository, tipoEvento);

        TipoEvento salvo = service.salvar(tipoEvento);

        assertSame(tipoEvento, salvo);
        verify(repository);
    }

    @Test
    void deveExcluirTipoEventoQuandoIdForValidoNoLimiarMinimo() {
        expect(tipoEvento.getId()).andStubReturn(1L);
        repository.delete(tipoEvento);
        expectLastCall().once();

        replay(repository, tipoEvento);

        service.excluir(tipoEvento);

        verify(repository);
    }

    @Test
    void deveLancarNullPointerExceptionAoExcluirTipoEventoComIdNulo() {
        expect(tipoEvento.getId()).andStubReturn(null);
        replay(repository, tipoEvento);

        assertThrows(NullPointerException.class, () -> service.excluir(tipoEvento));
        verify(repository);
    }

    @Test
    void deveLancarExcecaoQuandoNomeForVazioOuApenasEspacos() {
        expect(tipoEvento.getNome()).andStubReturn("   ");
        replay(repository, tipoEvento);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> service.validar(tipoEvento));
        assertEquals("Nome não pode ser vazio", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoNomeNaoContiverLetrasValidas() {
        expect(tipoEvento.getNome()).andStubReturn("12345 - @#$");
        replay(repository, tipoEvento);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> service.validar(tipoEvento));
        assertEquals("Nome deve conter letras válidas", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoNovoTipoEventoJaExistirComMesmoNome() {
        expect(tipoEvento.getId()).andStubReturn(null);
        expect(tipoEvento.getNome()).andStubReturn("Teste");
        expect(repository.existsByNomeIgnoreCase("Teste")).andReturn(true);

        replay(repository, tipoEvento);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> service.validar(tipoEvento));
        assertEquals("Já existe um tipo de evento com esse nome", ex.getMessage());
        verify(repository);
    }
}