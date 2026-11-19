package br.edu.ifsudestemg.eventsAPI.service.validator;

import br.edu.ifsudestemg.eventsAPI.api.exeption.RegraNegocioException;
import br.edu.ifsudestemg.eventsAPI.model.entity.Usuario;
import br.edu.ifsudestemg.eventsAPI.model.entity.UsuarioPessoaJuridica;
import br.edu.ifsudestemg.eventsAPI.model.repository.UsuarioPessoaJuridicaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.*;

class UsuarioPessoaJuridicaValidatorTest {

    private UsuarioPessoaJuridicaRepository repository;
    private UsuarioPessoaJuridicaValidator validator;
    private UsuarioPessoaJuridica usuarioPessoaJuridica;

    @BeforeEach
    void setUp() {
        repository = createMock(UsuarioPessoaJuridicaRepository.class);
        validator = new UsuarioPessoaJuridicaValidator(repository);
        usuarioPessoaJuridica = createNiceMock(UsuarioPessoaJuridica.class);
    }

    @Test
    void deveIgnorarValidacaoQuandoUsuarioNaoForPessoaJuridica() {
        Usuario outroUsuario = createNiceMock(Usuario.class);
        replay(repository, usuarioPessoaJuridica, outroUsuario);

        assertDoesNotThrow(() -> validator.validar(outroUsuario));
        verify(repository);
    }

    @Test
    void deveValidarNovoUsuarioPessoaJuridicaNoLimiarMinimoDeCaracteresComSucesso() {
        expect(usuarioPessoaJuridica.getId()).andStubReturn(null);
        expect(usuarioPessoaJuridica.getRazaoSocial()).andStubReturn("a");
        expect(usuarioPessoaJuridica.getCnpj()).andStubReturn("12345678000199");
        expect(usuarioPessoaJuridica.getInscricaoEstadualMunicipal()).andStubReturn("123456");

        expect(repository.existsByRazaoSocialIgnoreCase("a")).andReturn(false);
        expect(repository.existsByCnpj("12345678000199")).andReturn(false);
        expect(repository.existsByInscricaoEstadualMunicipal("123456")).andReturn(false);

        replay(repository, usuarioPessoaJuridica);

        assertDoesNotThrow(() -> validator.validar(usuarioPessoaJuridica));
        verify(repository);
    }

    @Test
    void deveValidarEdicaoDeUsuarioPessoaJuridicaExistenteSemVerificarDuplicidades() {
        expect(usuarioPessoaJuridica.getId()).andStubReturn(1L);
        expect(usuarioPessoaJuridica.getRazaoSocial()).andStubReturn("Teste");

        replay(repository, usuarioPessoaJuridica);

        assertDoesNotThrow(() -> validator.validar(usuarioPessoaJuridica));
        verify(repository);
    }

    @Test
    void deveLancarExcecaoQuandoRazaoSocialForVaziaOuApenasEspacos() {
        expect(usuarioPessoaJuridica.getRazaoSocial()).andStubReturn("   ");
        replay(repository, usuarioPessoaJuridica);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> validator.validar(usuarioPessoaJuridica));
        assertEquals("Nome não pode ser vazio", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoRazaoSocialNaoContiverLetrasValidas() {
        expect(usuarioPessoaJuridica.getRazaoSocial()).andStubReturn("12345 - @#$");
        replay(repository, usuarioPessoaJuridica);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> validator.validar(usuarioPessoaJuridica));
        assertEquals("Nome deve conter letras válidas", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoNaSegundaVerificacaoDeNomeQuandoSegundaChamadaDeRazaoSocialForVazia() {
        expect(usuarioPessoaJuridica.getRazaoSocial()).andReturn("a");
        expect(usuarioPessoaJuridica.getRazaoSocial()).andReturn("   ");
        replay(repository, usuarioPessoaJuridica);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> validator.validar(usuarioPessoaJuridica));
        assertEquals("Nome não pode ser vazio", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoNaSegundaVerificacaoDeNomeQuandoSegundaChamadaDeRazaoSocialNaoContiverLetras() {
        expect(usuarioPessoaJuridica.getRazaoSocial()).andReturn("a");
        expect(usuarioPessoaJuridica.getRazaoSocial()).andReturn("12345");
        replay(repository, usuarioPessoaJuridica);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> validator.validar(usuarioPessoaJuridica));
        assertEquals("Nome deve conter letras válidas", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoNovaPessoaJuridicaPossuirRazaoSocialJaCadastrada() {
        expect(usuarioPessoaJuridica.getId()).andStubReturn(null);
        expect(usuarioPessoaJuridica.getRazaoSocial()).andStubReturn("Teste");

        expect(repository.existsByRazaoSocialIgnoreCase("Teste")).andReturn(true);

        replay(repository, usuarioPessoaJuridica);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> validator.validar(usuarioPessoaJuridica));
        assertEquals("Razão social já cadastrada", ex.getMessage());
        verify(repository);
    }

    @Test
    void deveLancarExcecaoQuandoNovaPessoaJuridicaPossuirCnpjJaCadastrado() {
        expect(usuarioPessoaJuridica.getId()).andStubReturn(null);
        expect(usuarioPessoaJuridica.getRazaoSocial()).andStubReturn("Teste");
        expect(usuarioPessoaJuridica.getCnpj()).andStubReturn("12345678000199");

        expect(repository.existsByRazaoSocialIgnoreCase("Teste")).andReturn(false);
        expect(repository.existsByCnpj("12345678000199")).andReturn(true);

        replay(repository, usuarioPessoaJuridica);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> validator.validar(usuarioPessoaJuridica));
        assertEquals("CNPJ já cadastrado", ex.getMessage());
        verify(repository);
    }

    @Test
    void deveLancarExcecaoQuandoNovaPessoaJuridicaPossuirInscricaoEstadualMunicipalJaCadastrada() {
        expect(usuarioPessoaJuridica.getId()).andStubReturn(null);
        expect(usuarioPessoaJuridica.getRazaoSocial()).andStubReturn("Teste");
        expect(usuarioPessoaJuridica.getCnpj()).andStubReturn("12345678000199");
        expect(usuarioPessoaJuridica.getInscricaoEstadualMunicipal()).andStubReturn("123456");

        expect(repository.existsByRazaoSocialIgnoreCase("Teste")).andReturn(false);
        expect(repository.existsByCnpj("12345678000199")).andReturn(false);
        expect(repository.existsByInscricaoEstadualMunicipal("123456")).andReturn(true);

        replay(repository, usuarioPessoaJuridica);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> validator.validar(usuarioPessoaJuridica));
        assertEquals("Inscrição já cadastrada", ex.getMessage());
        verify(repository);
    }
}