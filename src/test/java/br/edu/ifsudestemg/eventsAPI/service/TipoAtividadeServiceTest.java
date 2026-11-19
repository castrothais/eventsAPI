package br.edu.ifsudestemg.eventsAPI.service;

import br.edu.ifsudestemg.eventsAPI.api.exeption.RegraNegocioException;
import br.edu.ifsudestemg.eventsAPI.model.entity.TipoAtividade;
import br.edu.ifsudestemg.eventsAPI.model.repository.TipoAtividadeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.*;

class TipoAtividadeServiceTest {

    private TipoAtividadeRepository repository;
    private TipoAtividadeService service;
    private TipoAtividade tipoAtividade;

    @BeforeEach
    void setUp() {
        repository = createMock(TipoAtividadeRepository.class);
        service = new TipoAtividadeService(repository);
        tipoAtividade = createNiceMock(TipoAtividade.class);
    }

    @Test
    void deveRetornarListaDeTipoAtividades() {
        expect(repository.findAll()).andReturn(List.of(tipoAtividade));
        replay(repository, tipoAtividade);

        List<TipoAtividade> resultado = service.getTipoAtividades();

        assertEquals(1, resultado.size());
        verify(repository);
    }

    @Test
    void deveRetornarTipoAtividadePorIdNoLimiarMinimoQuandoExistir() {
        expect(repository.findById(1L)).andReturn(Optional.of(tipoAtividade));
        replay(repository, tipoAtividade);

        Optional<TipoAtividade> resultado = service.getTipoAtividadesById(1L);

        assertTrue(resultado.isPresent());
        verify(repository);
    }

    @Test
    void deveSalvarNovoTipoAtividadeNoLimiarMinimoDeCaracteresComSucesso() {
        expect(tipoAtividade.getId()).andStubReturn(null);
        expect(tipoAtividade.getNome()).andStubReturn("a");

        expect(repository.existsByNomeIgnoreCase("a")).andReturn(false);
        expect(repository.save(tipoAtividade)).andReturn(tipoAtividade);

        replay(repository, tipoAtividade);

        TipoAtividade salvo = service.salvar(tipoAtividade);

        assertSame(tipoAtividade, salvo);
        verify(repository);
    }

    @Test
    void deveSalvarEdicaoDeTipoAtividadeExistenteSemValidarDuplicidadeDeNome() {
        expect(tipoAtividade.getId()).andStubReturn(1L);
        expect(tipoAtividade.getNome()).andStubReturn("Teste");

        expect(repository.save(tipoAtividade)).andReturn(tipoAtividade);

        replay(repository, tipoAtividade);

        TipoAtividade salvo = service.salvar(tipoAtividade);

        assertSame(tipoAtividade, salvo);
        verify(repository);
    }

    @Test
    void deveExcluirTipoAtividadeQuandoIdForValidoNoLimiarMinimo() {
        expect(tipoAtividade.getId()).andStubReturn(1L);
        repository.delete(tipoAtividade);
        expectLastCall().once();

        replay(repository, tipoAtividade);

        service.excluir(tipoAtividade);

        verify(repository);
    }

    @Test
    void deveLancarNullPointerExceptionAoExcluirTipoAtividadeComIdNulo() {
        expect(tipoAtividade.getId()).andStubReturn(null);
        replay(repository, tipoAtividade);

        assertThrows(NullPointerException.class, () -> service.excluir(tipoAtividade));
        verify(repository);
    }

    @Test
    void deveLancarExcecaoQuandoNomeForVazioOuApenasEspacos() {
        expect(tipoAtividade.getNome()).andStubReturn("   ");
        replay(repository, tipoAtividade);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> service.validar(tipoAtividade));
        assertEquals("Nome não pode ser vazio", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoNomeNaoContiverLetrasValidas() {
        expect(tipoAtividade.getNome()).andStubReturn("12345 - @#$");
        replay(repository, tipoAtividade);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> service.validar(tipoAtividade));
        assertEquals("Nome deve conter letras válidas", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoNovoTipoAtividadeJaExistirComMesmoNome() {
        expect(tipoAtividade.getId()).andStubReturn(null);
        expect(tipoAtividade.getNome()).andStubReturn("Teste");
        expect(repository.existsByNomeIgnoreCase("Teste")).andReturn(true);

        replay(repository, tipoAtividade);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> service.validar(tipoAtividade));
        assertEquals("Já existe um tipo de atividade com esse nome", ex.getMessage());
        verify(repository);
    }
}