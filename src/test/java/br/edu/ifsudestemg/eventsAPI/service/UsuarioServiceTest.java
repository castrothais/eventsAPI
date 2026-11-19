package br.edu.ifsudestemg.eventsAPI.service;

import br.edu.ifsudestemg.eventsAPI.api.exeption.RegraNegocioException;
import br.edu.ifsudestemg.eventsAPI.api.exeption.SenhaInvalidaException;
import br.edu.ifsudestemg.eventsAPI.model.entity.Carteira;
import br.edu.ifsudestemg.eventsAPI.model.entity.Perfil;
import br.edu.ifsudestemg.eventsAPI.model.entity.Usuario;
import br.edu.ifsudestemg.eventsAPI.model.repository.UsuarioRepository;
import br.edu.ifsudestemg.eventsAPI.service.validator.UsuarioValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.*;

class UsuarioServiceTest {

    private UsuarioRepository repository;
    private UsuarioValidator validator;
    private PasswordEncoder encoder;
    private CarteiraService carteiraService;
    private UsuarioService service;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        repository = createMock(UsuarioRepository.class);
        validator = createMock(UsuarioValidator.class);
        encoder = createMock(PasswordEncoder.class);
        carteiraService = createMock(CarteiraService.class);

        service = new UsuarioService(repository, List.of(validator));
        ReflectionTestUtils.setField(service, "encoder", encoder);
        ReflectionTestUtils.setField(service, "carteiraService", carteiraService);

        usuario = createNiceMock(Usuario.class);
    }

    @Test
    void deveRetornarListaDeUsuarios() {
        expect(repository.findAll()).andReturn(List.of(usuario));
        replay(repository, validator, encoder, carteiraService, usuario);

        List<Usuario> resultado = service.getUsuarios();

        assertEquals(1, resultado.size());
        verify(repository);
    }

    @Test
    void deveRetornarUsuarioPorIdNoLimiarMinimoQuandoExistir() {
        expect(repository.findById(1L)).andReturn(Optional.of(usuario));
        replay(repository, validator, encoder, carteiraService, usuario);

        Optional<Usuario> resultado = service.getUsuariosById(1L);

        assertTrue(resultado.isPresent());
        verify(repository);
    }

    @Test
    void deveSalvarNovoUsuarioECriarCarteiraComSucesso() {
        expect(usuario.getId()).andStubReturn(null);
        expect(usuario.getEmail()).andStubReturn("teste@email.com");

        validator.validar(usuario);
        expectLastCall().once();

        expect(repository.existsByEmailIgnoreCase("teste@email.com")).andReturn(false);
        expect(repository.save(usuario)).andReturn(usuario);
        expect(carteiraService.obterOuCriarCarteira(usuario)).andReturn(new Carteira());

        replay(repository, validator, encoder, carteiraService, usuario);

        Usuario salvo = service.salvar(usuario);

        assertSame(usuario, salvo);
        verify(repository, validator, carteiraService);
    }

    @Test
    void deveSalvarEdicaoDeUsuarioExistenteSemCriarCarteiraESemValidarEmailDuplicado() {
        expect(usuario.getId()).andStubReturn(1L);

        validator.validar(usuario);
        expectLastCall().once();

        expect(repository.save(usuario)).andReturn(usuario);

        replay(repository, validator, encoder, carteiraService, usuario);

        Usuario salvo = service.salvar(usuario);

        assertSame(usuario, salvo);
        verify(repository, validator, carteiraService);
    }

    @Test
    void deveExcluirUsuarioQuandoIdForValidoNoLimiarMinimo() {
        expect(usuario.getId()).andStubReturn(1L);
        repository.delete(usuario);
        expectLastCall().once();

        replay(repository, validator, encoder, carteiraService, usuario);

        service.excluir(usuario);

        verify(repository);
    }

    @Test
    void deveLancarNullPointerExceptionAoExcluirUsuarioComIdNulo() {
        expect(usuario.getId()).andStubReturn(null);
        replay(repository, validator, encoder, carteiraService, usuario);

        assertThrows(NullPointerException.class, () -> service.excluir(usuario));
        verify(repository);
    }


    @Test
    void deveLancarExcecaoQuandoNovoUsuarioPossuirEmailJaCadastrado() {
        expect(usuario.getId()).andStubReturn(null);
        expect(usuario.getEmail()).andStubReturn("teste@email.com");

        validator.validar(usuario);
        expectLastCall().once();

        expect(repository.existsByEmailIgnoreCase("teste@email.com")).andReturn(true);

        replay(repository, validator, encoder, carteiraService, usuario);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> service.validar(usuario));
        assertEquals("Email já cadastrado", ex.getMessage());
        verify(repository, validator);
    }


    @Test
    void deveValidarSenhaForteNoLimiarMinimoDeSeisCaracteresComSucesso() {
        replay(repository, validator, encoder, carteiraService, usuario);

        assertDoesNotThrow(() -> service.validarSenhaForte("Ab1@xy"));
    }

    @Test
    void deveLancarExcecaoQuandoSenhaForNula() {
        replay(repository, validator, encoder, carteiraService, usuario);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> service.validarSenhaForte(null));
        assertEquals("Senha deve ter no mínimo 6 caracteres", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoSenhaTiverCincoCaracteresNoLimiarMinimoDeViolacao() {
        replay(repository, validator, encoder, carteiraService, usuario);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> service.validarSenhaForte("Ab1@x"));
        assertEquals("Senha deve ter no mínimo 6 caracteres", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoSenhaNaoContiverNumero() {
        replay(repository, validator, encoder, carteiraService, usuario);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> service.validarSenhaForte("Abc@xy"));
        assertEquals("Senha deve conter número, maiúscula, minúscula e símbolo", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoSenhaNaoContiverLetraMaiuscula() {
        replay(repository, validator, encoder, carteiraService, usuario);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> service.validarSenhaForte("abc1@x"));
        assertEquals("Senha deve conter número, maiúscula, minúscula e símbolo", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoSenhaNaoContiverLetraMinuscula() {
        replay(repository, validator, encoder, carteiraService, usuario);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> service.validarSenhaForte("ABC1@X"));
        assertEquals("Senha deve conter número, maiúscula, minúscula e símbolo", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoSenhaNaoContiverSimbolo() {
        replay(repository, validator, encoder, carteiraService, usuario);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> service.validarSenhaForte("Abc123"));
        assertEquals("Senha deve conter número, maiúscula, minúscula e símbolo", ex.getMessage());
    }

    @Test
    void deveAutenticarUsuarioQuandoSenhaCorresponder() {
        expect(usuario.getEmail()).andStubReturn("teste@email.com");
        expect(usuario.getSenha()).andStubReturn("senhaTeste");
        expect(usuario.getPerfis()).andStubReturn(List.of(Perfil.MEDIADOR));

        expect(repository.findByEmail("teste@email.com")).andReturn(Optional.of(usuario));
        expect(encoder.matches("Ab1@xy", "senhaTeste")).andReturn(true);

        replay(repository, validator, encoder, carteiraService, usuario);

        UserDetails userDetails = service.autenticar("Ab1@xy", "teste@email.com");

        assertNotNull(userDetails);
        assertEquals("teste@email.com", userDetails.getUsername());
        verify(repository, encoder);
    }

    @Test
    void deveLancarSenhaInvalidaExceptionQuandoSenhaNaoCorresponderNaAutenticacao() {
        expect(usuario.getEmail()).andStubReturn("teste@email.com");
        expect(usuario.getSenha()).andStubReturn("senhaTeste");
        expect(usuario.getPerfis()).andStubReturn(List.of(Perfil.MEDIADOR));

        expect(repository.findByEmail("teste@email.com")).andReturn(Optional.of(usuario));
        expect(encoder.matches("senhaErrada", "senhaTeste")).andReturn(false);

        replay(repository, validator, encoder, carteiraService, usuario);

        assertThrows(SenhaInvalidaException.class, () -> service.autenticar("senhaErrada", "teste@email.com"));
        verify(repository, encoder);
    }

    @Test
    void deveLancarUsernameNotFoundExceptionQuandoEmailNaoForEncontradoNoLoadUserByUsername() {
        expect(repository.findByEmail("inexistente@email.com")).andReturn(Optional.empty());

        replay(repository, validator, encoder, carteiraService, usuario);

        UsernameNotFoundException ex = assertThrows(
                UsernameNotFoundException.class,
                () -> service.loadUserByUsername("inexistente@email.com")
        );
        assertEquals("Usuário não encontrado", ex.getMessage());
        verify(repository);
    }

    @Test
    void deveRetornarUsuarioQuandoBuscarPorEmailExistente() {
        expect(repository.findByEmail("teste@email.com")).andReturn(Optional.of(usuario));

        replay(repository, validator, encoder, carteiraService, usuario);

        Usuario resultado = service.buscarPorEmail("teste@email.com");

        assertSame(usuario, resultado);
        verify(repository);
    }

    @Test
    void deveLancarUsernameNotFoundExceptionQuandoBuscarPorEmailInexistente() {
        expect(repository.findByEmail("naoexiste@email.com")).andReturn(Optional.empty());

        replay(repository, validator, encoder, carteiraService, usuario);

        UsernameNotFoundException ex = assertThrows(
                UsernameNotFoundException.class,
                () -> service.buscarPorEmail("naoexiste@email.com")
        );
        assertEquals("Usuário não encontrado", ex.getMessage());
        verify(repository);
    }
}