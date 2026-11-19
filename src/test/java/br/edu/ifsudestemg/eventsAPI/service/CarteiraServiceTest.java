package br.edu.ifsudestemg.eventsAPI.service;

import br.edu.ifsudestemg.eventsAPI.api.exeption.RegraNegocioException;
import br.edu.ifsudestemg.eventsAPI.model.entity.*;
import br.edu.ifsudestemg.eventsAPI.model.repository.CarteiraRepository;
import br.edu.ifsudestemg.eventsAPI.model.repository.MovimentacaoCarteiraRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.*;

class CarteiraServiceTest {

    private CarteiraRepository carteiraRepository;
    private MovimentacaoCarteiraRepository movimentacaoRepository;
    private CarteiraService service;

    private Usuario usuario;
    private Ingresso ingresso;
    private Carteira carteira;

    @BeforeEach
    void setUp() {
        carteiraRepository = createMock(CarteiraRepository.class);
        movimentacaoRepository = createMock(MovimentacaoCarteiraRepository.class);
        service = new CarteiraService(carteiraRepository, movimentacaoRepository);

        usuario = createNiceMock(Usuario.class);
        ingresso = createNiceMock(Ingresso.class);
        carteira = new Carteira();
        carteira.setUsuario(usuario);
        carteira.setSaldo(new BigDecimal("0.01"));
    }


    @Test
    void deveRetornarCarteiraExistenteQuandoUsuarioJaPossuirCarteira() {
        expect(usuario.getId()).andStubReturn(1L);
        expect(carteiraRepository.findByUsuario_Id(1L)).andReturn(Optional.of(carteira));

        replay(carteiraRepository, movimentacaoRepository, usuario, ingresso);

        Carteira resultado = service.obterOuCriarCarteira(usuario);

        assertSame(carteira, resultado);
        verify(carteiraRepository);
    }

    @Test
    void deveCriarESalvarNovaCarteiraQuandoUsuarioNaoPossuirCarteira() {
        expect(usuario.getId()).andStubReturn(1L);
        expect(carteiraRepository.findByUsuario_Id(1L)).andReturn(Optional.empty());
        expect(carteiraRepository.save(anyObject(Carteira.class)))
                .andAnswer(() -> (Carteira) getCurrentArguments()[0]);

        replay(carteiraRepository, movimentacaoRepository, usuario, ingresso);

        Carteira resultado = service.obterOuCriarCarteira(usuario);

        assertNotNull(resultado);
        assertSame(usuario, resultado.getUsuario());
        assertEquals(BigDecimal.ZERO, resultado.getSaldo());
        verify(carteiraRepository);
    }

    @Test
    void deveRetornarSaldoDaCarteiraQuandoCarteiraExistir() {
        expect(carteiraRepository.findByUsuario_Id(1L)).andReturn(Optional.of(carteira));

        replay(carteiraRepository, movimentacaoRepository, usuario, ingresso);

        BigDecimal saldo = service.consultarSaldo(1L);

        assertEquals(new BigDecimal("0.01"), saldo);
        verify(carteiraRepository);
    }

    @Test
    void deveRetornarSaldoZeroQuandoCarteiraNaoExistir() {
        expect(carteiraRepository.findByUsuario_Id(1L)).andReturn(Optional.empty());

        replay(carteiraRepository, movimentacaoRepository, usuario, ingresso);

        BigDecimal saldo = service.consultarSaldo(1L);

        assertEquals(BigDecimal.ZERO, saldo);
        verify(carteiraRepository);
    }


    @Test
    void deveLancarExcecaoAoCreditarValorNulo() {
        replay(carteiraRepository, movimentacaoRepository, usuario, ingresso);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class,
                () -> service.creditar(usuario, null, ingresso));

        assertEquals("O valor da movimentação deve ser maior que zero.", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoAoCreditarValorZeroOuNegativo() {
        replay(carteiraRepository, movimentacaoRepository, usuario, ingresso);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class,
                () -> service.creditar(usuario, BigDecimal.ZERO, ingresso));

        assertEquals("O valor da movimentação deve ser maior que zero.", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoAoCreditarQuandoIngressoJaGerouCredito() {
        expect(ingresso.getId()).andStubReturn(1L);
        expect(movimentacaoRepository.existsByIngresso_IdAndTipo(1L, TipoMovimentacao.CREDITO))
                .andReturn(true);

        replay(carteiraRepository, movimentacaoRepository, usuario, ingresso);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class,
                () -> service.creditar(usuario, new BigDecimal("0.01"), ingresso));

        assertEquals("Este ingresso já gerou um crédito na carteira.", ex.getMessage());
        verify(movimentacaoRepository);
    }

    @Test
    void deveCreditarValorComIngressoValidoERegistrarMovimentacao() {
        expect(usuario.getId()).andStubReturn(1L);
        expect(ingresso.getId()).andStubReturn(2L);

        expect(movimentacaoRepository.existsByIngresso_IdAndTipo(2L, TipoMovimentacao.CREDITO))
                .andReturn(false);
        expect(carteiraRepository.findByUsuario_Id(1L)).andReturn(Optional.of(carteira));
        expect(carteiraRepository.save(carteira)).andReturn(carteira);
        expect(movimentacaoRepository.save(anyObject(MovimentacaoCarteira.class)))
                .andAnswer(() -> (MovimentacaoCarteira) getCurrentArguments()[0]);

        replay(carteiraRepository, movimentacaoRepository, usuario, ingresso);

        service.creditar(usuario, new BigDecimal("0.01"), ingresso);

        assertEquals(new BigDecimal("0.02"), carteira.getSaldo());
        verify(carteiraRepository, movimentacaoRepository);
    }

    @Test
    void deveCreditarValorQuandoIngressoForNulo() {
        expect(usuario.getId()).andStubReturn(1L);

        expect(carteiraRepository.findByUsuario_Id(1L)).andReturn(Optional.of(carteira));
        expect(carteiraRepository.save(carteira)).andReturn(carteira);
        expect(movimentacaoRepository.save(anyObject(MovimentacaoCarteira.class)))
                .andAnswer(() -> (MovimentacaoCarteira) getCurrentArguments()[0]);

        replay(carteiraRepository, movimentacaoRepository, usuario, ingresso);

        service.creditar(usuario, new BigDecimal("0.01"), null);

        assertEquals(new BigDecimal("0.02"), carteira.getSaldo());
        verify(carteiraRepository, movimentacaoRepository);
    }


    @Test
    void deveLancarExcecaoAoDebitarValorInvalido() {
        replay(carteiraRepository, movimentacaoRepository, usuario, ingresso);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class,
                () -> service.debitar(usuario, new BigDecimal("-0.01"), ingresso));

        assertEquals("O valor da movimentação deve ser maior que zero.", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoAoDebitarQuandoIngressoJaPossuirDebito() {
        expect(ingresso.getId()).andStubReturn(1L);
        expect(movimentacaoRepository.existsByIngresso_IdAndTipo(1L, TipoMovimentacao.DEBITO))
                .andReturn(true);

        replay(carteiraRepository, movimentacaoRepository, usuario, ingresso);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class,
                () -> service.debitar(usuario, new BigDecimal("0.01"), ingresso));

        assertEquals("Este ingresso já possui um débito registrado na carteira.", ex.getMessage());
        verify(movimentacaoRepository);
    }

    @Test
    void deveLancarExcecaoAoDebitarQuandoCarteiraNaoForEncontrada() {
        expect(usuario.getId()).andStubReturn(1L);
        expect(carteiraRepository.findByUsuarioIdComBloqueio(1L)).andReturn(Optional.empty());

        replay(carteiraRepository, movimentacaoRepository, usuario, ingresso);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class,
                () -> service.debitar(usuario, new BigDecimal("0.01"), null));

        assertEquals("Carteira não encontrada.", ex.getMessage());
        verify(carteiraRepository);
    }

    @Test
    void deveLancarExcecaoAoDebitarQuandoSaldoForInsuficiente() {
        expect(usuario.getId()).andStubReturn(1L);
        expect(carteiraRepository.findByUsuarioIdComBloqueio(1L)).andReturn(Optional.of(carteira));

        replay(carteiraRepository, movimentacaoRepository, usuario, ingresso);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class,
                () -> service.debitar(usuario, new BigDecimal("0.02"), null));

        assertEquals("Saldo insuficiente na carteira.", ex.getMessage());
        verify(carteiraRepository);
    }

    @Test
    void deveDebitarValorComIngressoValidoERegistrarMovimentacao() {
        expect(usuario.getId()).andStubReturn(1L);
        expect(ingresso.getId()).andStubReturn(2L);

        expect(movimentacaoRepository.existsByIngresso_IdAndTipo(2L, TipoMovimentacao.DEBITO))
                .andReturn(false);
        expect(carteiraRepository.findByUsuarioIdComBloqueio(1L)).andReturn(Optional.of(carteira));
        expect(carteiraRepository.save(carteira)).andReturn(carteira);
        expect(movimentacaoRepository.save(anyObject(MovimentacaoCarteira.class)))
                .andAnswer(() -> (MovimentacaoCarteira) getCurrentArguments()[0]);

        replay(carteiraRepository, movimentacaoRepository, usuario, ingresso);

        service.debitar(usuario, new BigDecimal("0.01"), ingresso);

        assertEquals(new BigDecimal("0.00"), carteira.getSaldo());
        verify(carteiraRepository, movimentacaoRepository);
    }

    @Test
    void deveDebitarValorExatoDoSaldoQuandoIngressoForNulo() {
        expect(usuario.getId()).andStubReturn(1L);

        expect(carteiraRepository.findByUsuarioIdComBloqueio(1L)).andReturn(Optional.of(carteira));
        expect(carteiraRepository.save(carteira)).andReturn(carteira);
        expect(movimentacaoRepository.save(anyObject(MovimentacaoCarteira.class)))
                .andAnswer(() -> (MovimentacaoCarteira) getCurrentArguments()[0]);

        replay(carteiraRepository, movimentacaoRepository, usuario, ingresso);

        service.debitar(usuario, new BigDecimal("0.01"), null);

        assertEquals(new BigDecimal("0.00"), carteira.getSaldo());
        verify(carteiraRepository, movimentacaoRepository);
    }


    @Test
    void deveRetornarListaDeMovimentacoesOrdenadasPorDataDecrescente() {
        MovimentacaoCarteira mov = new MovimentacaoCarteira();
        expect(movimentacaoRepository.findByCarteira_IdOrderByDataHoraDesc(2L))
                .andReturn(List.of(mov));

        replay(carteiraRepository, movimentacaoRepository, usuario, ingresso);

        List<MovimentacaoCarteira> resultado = service.consultarMovimentacoes(2L);

        assertEquals(1, resultado.size());
        verify(movimentacaoRepository);
    }
}