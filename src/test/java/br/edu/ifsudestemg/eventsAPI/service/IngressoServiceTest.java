package br.edu.ifsudestemg.eventsAPI.service;

import br.edu.ifsudestemg.eventsAPI.model.entity.Ingresso;
import br.edu.ifsudestemg.eventsAPI.model.repository.IngressoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.*;

class IngressoServiceTest {

    private IngressoRepository repository;
    private IngressoService service;
    private Ingresso ingresso;

    @BeforeEach
    void setUp() {
        repository = createMock(IngressoRepository.class);
        service = new IngressoService(repository);
        ingresso = createNiceMock(Ingresso.class);
    }

    @Test
    void deveRetornarListaDeIngressosQuandoExistiremRegistros() {
        expect(repository.findAll()).andReturn(List.of(ingresso));
        replay(repository, ingresso);

        List<Ingresso> resultado = service.getIngressos();

        assertEquals(1, resultado.size());
        verify(repository);
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistiremIngressos() {
        expect(repository.findAll()).andReturn(Collections.emptyList());
        replay(repository, ingresso);

        List<Ingresso> resultado = service.getIngressos();

        assertTrue(resultado.isEmpty());
        verify(repository);
    }

    @Test
    void deveRetornarIngressoPorIdQuandoExistir() {
        expect(repository.findById(1L)).andReturn(Optional.of(ingresso));
        replay(repository, ingresso);

        Optional<Ingresso> resultado = service.getIngressosById(1L);

        assertTrue(resultado.isPresent());
        verify(repository);
    }

    @Test
    void deveRetornarOptionalVazioQuandoIngressoPorIdNaoExistir() {
        expect(repository.findById(1L)).andReturn(Optional.empty());
        replay(repository, ingresso);

        Optional<Ingresso> resultado = service.getIngressosById(1L);

        assertTrue(resultado.isEmpty());
        verify(repository);
    }

    @Test
    void deveSalvarIngressoComSucesso() {
        expect(repository.save(ingresso)).andReturn(ingresso);
        replay(repository, ingresso);

        Ingresso salvo = service.salvar(ingresso);

        assertSame(ingresso, salvo);
        verify(repository);
    }

    @Test
    void deveExcluirIngressoQuandoIdForValido() {
        expect(ingresso.getId()).andStubReturn(1L);
        repository.delete(ingresso);
        expectLastCall().once();

        replay(repository, ingresso);

        service.excluir(ingresso);

        verify(repository);
    }

    @Test
    void deveLancarExcecaoAoExcluirIngressoComIdNulo() {
        expect(ingresso.getId()).andStubReturn(null);
        replay(repository, ingresso);

        assertThrows(NullPointerException.class, () -> service.excluir(ingresso));
        verify(repository);
    }

    @Test
    void deveLancarExcecaoAoExcluirQuandoIngressoForNulo() {
        replay(repository, ingresso);

        assertThrows(NullPointerException.class, () -> service.excluir(null));
        verify(repository);
    }
}