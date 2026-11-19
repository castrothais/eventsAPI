package br.edu.ifsudestemg.eventsAPI.service.validator;

import br.edu.ifsudestemg.eventsAPI.api.exeption.RegraNegocioException;
import br.edu.ifsudestemg.eventsAPI.model.entity.Usuario;
import br.edu.ifsudestemg.eventsAPI.model.entity.UsuarioPessoaFisica;
import br.edu.ifsudestemg.eventsAPI.model.repository.UsuarioPessoaFisicaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.*;

class UsuarioPessoaFisicaValidatorTest {

    private UsuarioPessoaFisicaRepository repository;
    private UsuarioPessoaFisicaValidator validator;
    private UsuarioPessoaFisica usuarioPessoaFisica;

    @BeforeEach
    void setUp() {
        repository = createMock(UsuarioPessoaFisicaRepository.class);
        validator = new UsuarioPessoaFisicaValidator(repository);
        usuarioPessoaFisica = createNiceMock(UsuarioPessoaFisica.class);
    }

    @Test
    void deveIgnorarValidacaoQuandoUsuarioNaoForPessoaFisica() {
        Usuario outroUsuario = createNiceMock(Usuario.class);
        replay(repository, usuarioPessoaFisica, outroUsuario);

        assertDoesNotThrow(() -> validator.validar(outroUsuario));
        verify(repository);
    }

    @Test
    void deveValidarNovoUsuarioPessoaFisicaNoLimiarMinimoDeCaracteresComSucesso() {
        expect(usuarioPessoaFisica.getId()).andStubReturn(null);
        expect(usuarioPessoaFisica.getNome()).andStubReturn("a");
        expect(usuarioPessoaFisica.getCpf()).andStubReturn("12345678901");

        expect(repository.existsByCpf("12345678901")).andReturn(false);

        replay(repository, usuarioPessoaFisica);

        assertDoesNotThrow(() -> validator.validar(usuarioPessoaFisica));
        verify(repository);
    }

    @Test
    void deveValidarEdicaoDeUsuarioPessoaFisicaExistenteSemVerificarCpfDuplicado() {
        expect(usuarioPessoaFisica.getId()).andStubReturn(1L);
        expect(usuarioPessoaFisica.getNome()).andStubReturn("Teste");

        replay(repository, usuarioPessoaFisica);

        assertDoesNotThrow(() -> validator.validar(usuarioPessoaFisica));
        verify(repository);
    }

    @Test
    void deveLancarExcecaoQuandoNomeForVazioOuApenasEspacos() {
        expect(usuarioPessoaFisica.getNome()).andStubReturn("   ");
        replay(repository, usuarioPessoaFisica);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> validator.validar(usuarioPessoaFisica));
        assertEquals("Nome não pode ser vazio", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoNomeNaoContiverLetrasValidas() {
        expect(usuarioPessoaFisica.getNome()).andStubReturn("12345 - @#$");
        replay(repository, usuarioPessoaFisica);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> validator.validar(usuarioPessoaFisica));
        assertEquals("Nome deve conter letras válidas", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoNovoUsuarioPessoaFisicaPossuirCpfJaCadastrado() {
        expect(usuarioPessoaFisica.getId()).andStubReturn(null);
        expect(usuarioPessoaFisica.getNome()).andStubReturn("Teste");
        expect(usuarioPessoaFisica.getCpf()).andStubReturn("12345678901");

        expect(repository.existsByCpf("12345678901")).andReturn(true);

        replay(repository, usuarioPessoaFisica);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> validator.validar(usuarioPessoaFisica));
        assertEquals("CPF já cadastrado", ex.getMessage());
        verify(repository);
    }
}