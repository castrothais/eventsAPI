package br.edu.ifsudestemg.eventsAPI.api.controller;

import br.edu.ifsudestemg.eventsAPI.api.dto.IngressoDTO;
import br.edu.ifsudestemg.eventsAPI.api.dto.PagamentoIngressoDTO;
import br.edu.ifsudestemg.eventsAPI.api.exeption.RegraNegocioException;
import br.edu.ifsudestemg.eventsAPI.model.entity.Evento;
import br.edu.ifsudestemg.eventsAPI.model.entity.Ingresso;
import br.edu.ifsudestemg.eventsAPI.model.entity.StatusIngresso;
import br.edu.ifsudestemg.eventsAPI.model.entity.Usuario;
import br.edu.ifsudestemg.eventsAPI.service.CarteiraService;
import br.edu.ifsudestemg.eventsAPI.service.EventoService;
import br.edu.ifsudestemg.eventsAPI.service.IngressoService;
import br.edu.ifsudestemg.eventsAPI.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.*;

class IngressoControllerTest {

    private IngressoService service;
    private EventoService eventoService;
    private UsuarioService usuarioService;
    private CarteiraService carteiraService;
    private IngressoController controller;

    private Usuario participante;
    private Evento evento;
    private Ingresso ingresso;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        service = createMock(IngressoService.class);
        eventoService = createMock(EventoService.class);
        usuarioService = createMock(UsuarioService.class);
        carteiraService = createMock(CarteiraService.class);

        controller = new IngressoController(service, eventoService, usuarioService, carteiraService);

        participante = createNiceMock(Usuario.class);
        evento = createNiceMock(Evento.class);
        ingresso = createNiceMock(Ingresso.class);
        authentication = createNiceMock(Authentication.class);
    }

    @Test
    void deveRetornarStatusOkComListaDeIngressosDto() {
        expect(participante.getId()).andStubReturn(1L);
        expect(evento.getId()).andStubReturn(2L);
        expect(ingresso.getId()).andStubReturn(1L);
        expect(ingresso.getStatus()).andStubReturn(StatusIngresso.RESERVADO);
        expect(ingresso.getParticipante()).andStubReturn(participante);
        expect(ingresso.getEvento()).andStubReturn(evento);

        expect(service.getIngressos()).andReturn(List.of(ingresso));

        replay(service, eventoService, usuarioService, carteiraService, participante, evento, ingresso, authentication);

        ResponseEntity response = controller.get();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<?> body = (List<?>) response.getBody();
        assertNotNull(body);
        assertEquals(1, body.size());
        verify(service);
    }


    @Test
    void deveRetornarStatusNotFoundQuandoIngressoPorIdNaoExistir() {
        expect(service.getIngressosById(1L)).andReturn(Optional.empty());

        replay(service, eventoService, usuarioService, carteiraService, participante, evento, ingresso, authentication);

        ResponseEntity response = controller.get(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Ingresso não encontrado", response.getBody());
        verify(service);
    }

    @Test
    void deveRetornarStatusOkQuandoIngressoPorIdExistir() {
        expect(participante.getId()).andStubReturn(1L);
        expect(evento.getId()).andStubReturn(2L);
        expect(ingresso.getId()).andStubReturn(1L);
        expect(ingresso.getStatus()).andStubReturn(StatusIngresso.RESERVADO);
        expect(ingresso.getParticipante()).andStubReturn(participante);
        expect(ingresso.getEvento()).andStubReturn(evento);

        expect(service.getIngressosById(1L)).andReturn(Optional.of(ingresso));

        replay(service, eventoService, usuarioService, carteiraService, participante, evento, ingresso, authentication);

        ResponseEntity response = controller.get(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(service);
    }


    @Test
    void deveCriarIngressoERetornarStatusCreatedComSucesso() {
        IngressoDTO dto = criarDtoValido();

        expect(participante.getId()).andStubReturn(1L);
        expect(evento.getId()).andStubReturn(2L);

        expect(usuarioService.getUsuariosById(1L)).andReturn(Optional.of(participante));
        expect(eventoService.getEventosById(2L)).andReturn(Optional.of(evento));
        expect(service.salvar(anyObject(Ingresso.class))).andAnswer(() -> (Ingresso) getCurrentArguments()[0]);

        replay(service, eventoService, usuarioService, carteiraService, participante, evento, ingresso, authentication);

        ResponseEntity response = controller.post(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(service, usuarioService, eventoService);
    }

    @Test
    void deveRetornarBadRequestNoPostQuandoParticipanteNaoForEncontrado() {
        IngressoDTO dto = criarDtoValido();

        expect(usuarioService.getUsuariosById(1L)).andReturn(Optional.empty());

        replay(service, eventoService, usuarioService, carteiraService, participante, evento, ingresso, authentication);

        ResponseEntity response = controller.post(dto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getBody() != null ? response.getStatusCode() : null);
        assertEquals("Participante não encontrado", response.getBody());
        verify(usuarioService);
    }

    @Test
    void deveRetornarBadRequestNoPostQuandoEventoNaoForEncontrado() {
        IngressoDTO dto = criarDtoValido();

        expect(usuarioService.getUsuariosById(1L)).andReturn(Optional.of(participante));
        expect(eventoService.getEventosById(2L)).andReturn(Optional.empty());

        replay(service, eventoService, usuarioService, carteiraService, participante, evento, ingresso, authentication);

        ResponseEntity response = controller.post(dto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Evento não encontrado", response.getBody());
        verify(usuarioService, eventoService);
    }


    @Test
    void deveRetornarNotFoundNoPagamentoQuandoIngressoOptForVazio() {
        PagamentoIngressoDTO dto = new PagamentoIngressoDTO();
        expect(service.getIngressosById(1L)).andReturn(Optional.empty());

        replay(service, eventoService, usuarioService, carteiraService, participante, evento, ingresso, authentication);

        ResponseEntity response = controller.pagarIngresso(1L, dto, authentication);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(service);
    }

    @Test
    void deveRetornarForbiddenNoPagamentoQuandoIdParticipanteForDistintoDoUsuario() {
        Usuario outroUsuario = createNiceMock(Usuario.class);
        PagamentoIngressoDTO dto = new PagamentoIngressoDTO();

        expect(authentication.getName()).andStubReturn("outro@email.com");
        expect(participante.getId()).andStubReturn(1L);
        expect(outroUsuario.getId()).andStubReturn(2L);
        expect(ingresso.getParticipante()).andStubReturn(participante);

        expect(service.getIngressosById(1L)).andReturn(Optional.of(ingresso));
        expect(usuarioService.buscarPorEmail("outro@email.com")).andReturn(outroUsuario);

        replay(service, eventoService, usuarioService, carteiraService, participante, outroUsuario, evento, ingresso, authentication);

        ResponseEntity response = controller.pagarIngresso(1L, dto, authentication);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(service, usuarioService);
    }

    @Test
    void deveRetornarBadRequestNoPagamentoQuandoEventoEstiverCancelado() {
        PagamentoIngressoDTO dto = new PagamentoIngressoDTO();

        expect(authentication.getName()).andStubReturn("user@email.com");
        expect(participante.getId()).andStubReturn(1L);
        expect(evento.isCancelado()).andStubReturn(true);
        expect(ingresso.getParticipante()).andStubReturn(participante);
        expect(ingresso.getEvento()).andStubReturn(evento);

        expect(service.getIngressosById(1L)).andReturn(Optional.of(ingresso));
        expect(usuarioService.buscarPorEmail("user@email.com")).andReturn(participante);

        replay(service, eventoService, usuarioService, carteiraService, participante, evento, ingresso, authentication);

        ResponseEntity response = controller.pagarIngresso(1L, dto, authentication);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Não é possível pagar um ingresso de evento cancelado.", response.getBody());
    }

    @Test
    void deveRetornarBadRequestNoPagamentoQuandoStatusIngressoForDiferenteDeReservado() {
        PagamentoIngressoDTO dto = new PagamentoIngressoDTO();

        expect(authentication.getName()).andStubReturn("user@email.com");
        expect(participante.getId()).andStubReturn(1L);
        expect(evento.isCancelado()).andStubReturn(false);
        expect(ingresso.getParticipante()).andStubReturn(participante);
        expect(ingresso.getEvento()).andStubReturn(evento);
        expect(ingresso.getStatus()).andStubReturn(StatusIngresso.PAGO);

        expect(service.getIngressosById(1L)).andReturn(Optional.of(ingresso));
        expect(usuarioService.buscarPorEmail("user@email.com")).andReturn(participante);

        replay(service, eventoService, usuarioService, carteiraService, participante, evento, ingresso, authentication);

        ResponseEntity response = controller.pagarIngresso(1L, dto, authentication);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Este ingresso não está disponível para pagamento.", response.getBody());
    }

    @Test
    void deveRetornarBadRequestNoPagamentoQuandoTipoIngressoForNulo() {
        PagamentoIngressoDTO dto = new PagamentoIngressoDTO();
        dto.setTipoIngresso(null);

        configurarCenarioBasePagamento(new BigDecimal("0.01"));

        replay(service, eventoService, usuarioService, carteiraService, participante, evento, ingresso, authentication);

        ResponseEntity response = controller.pagarIngresso(1L, dto, authentication);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Informe o tipo de ingresso.", response.getBody());
    }

    @Test
    void deveRetornarBadRequestNoPagamentoQuandoTipoIngressoForStringVaziaDefault() {
        PagamentoIngressoDTO dto = new PagamentoIngressoDTO();
        dto.setTipoIngresso("");

        configurarCenarioBasePagamento(new BigDecimal("0.01"));

        replay(service, eventoService, usuarioService, carteiraService, participante, evento, ingresso, authentication);

        ResponseEntity response = controller.pagarIngresso(1L, dto, authentication);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Tipo de ingresso inválido.", response.getBody());
    }

    @Test
    void deveRetornarBadRequestNoPagamentoQuandoFormaPagamentoForNula() {
        PagamentoIngressoDTO dto = new PagamentoIngressoDTO();
        dto.setTipoIngresso("inteira");
        dto.setFormaPagamento(null);

        configurarCenarioBasePagamento(new BigDecimal("0.01"));

        replay(service, eventoService, usuarioService, carteiraService, participante, evento, ingresso, authentication);

        ResponseEntity response = controller.pagarIngresso(1L, dto, authentication);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Informe a forma de pagamento.", response.getBody());
    }

    @Test
    void deveRetornarBadRequestNoPagamentoQuandoFormaPagamentoForStringVaziaDefault() {
        PagamentoIngressoDTO dto = new PagamentoIngressoDTO();
        dto.setTipoIngresso("inteira");
        dto.setFormaPagamento("");

        configurarCenarioBasePagamento(new BigDecimal("0.01"));

        replay(service, eventoService, usuarioService, carteiraService, participante, evento, ingresso, authentication);

        ResponseEntity response = controller.pagarIngresso(1L, dto, authentication);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Forma de pagamento inválida.", response.getBody());
    }

    @Test
    void devePagarIngressoInteiraComCarteiraQuandoValorIngressoForZeroPontoZeroUmNoLimiarMinimo() {
        PagamentoIngressoDTO dto = new PagamentoIngressoDTO();
        dto.setTipoIngresso("inteira");
        dto.setFormaPagamento("CARTEIRA");

        BigDecimal valorLimiteMinimo = new BigDecimal("0.01");
        configurarCenarioBasePagamento(valorLimiteMinimo);

        carteiraService.debitar(participante, valorLimiteMinimo, ingresso);
        expectLastCall().once();

        ingresso.setTipoIngresso("inteira");
        expectLastCall().once();
        ingresso.setValor(valorLimiteMinimo);
        expectLastCall().once();
        ingresso.setStatus(StatusIngresso.PAGO);
        expectLastCall().once();

        expect(service.salvar(ingresso)).andReturn(ingresso);

        replay(service, eventoService, usuarioService, carteiraService, participante, evento, ingresso, authentication);

        ResponseEntity response = controller.pagarIngresso(1L, dto, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(service, carteiraService, ingresso);
    }

    @Test
    void devePagarIngressoInteiraComCarteiraSemDebitarQuandoValorIngressoForZero() {
        PagamentoIngressoDTO dto = new PagamentoIngressoDTO();
        dto.setTipoIngresso("inteira");
        dto.setFormaPagamento("CARTEIRA");

        BigDecimal valorZero = BigDecimal.ZERO;
        configurarCenarioBasePagamento(valorZero);

        ingresso.setTipoIngresso("inteira");
        expectLastCall().once();
        ingresso.setValor(valorZero);
        expectLastCall().once();
        ingresso.setStatus(StatusIngresso.PAGO);
        expectLastCall().once();

        expect(service.salvar(ingresso)).andReturn(ingresso);

        replay(service, eventoService, usuarioService, carteiraService, participante, evento, ingresso, authentication);

        ResponseEntity response = controller.pagarIngresso(1L, dto, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(service, carteiraService, ingresso);
    }

    @Test
    void devePagarIngressoMeiaComPixDividindoValorPelaMetade() {
        PagamentoIngressoDTO dto = new PagamentoIngressoDTO();
        dto.setTipoIngresso("meia");
        dto.setFormaPagamento("PIX");

        configurarCenarioBasePagamento(new BigDecimal("0.02"));
        BigDecimal valorMeia = new BigDecimal("0.01");

        ingresso.setTipoIngresso("meia");
        expectLastCall().once();
        ingresso.setValor(valorMeia);
        expectLastCall().once();
        ingresso.setStatus(StatusIngresso.PAGO);
        expectLastCall().once();

        expect(service.salvar(ingresso)).andReturn(ingresso);

        replay(service, eventoService, usuarioService, carteiraService, participante, evento, ingresso, authentication);

        ResponseEntity response = controller.pagarIngresso(1L, dto, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(service, carteiraService, ingresso);
    }

    @Test
    void devePagarIngressoInteiraComPixSemDebitarNaCarteira() {
        PagamentoIngressoDTO dto = new PagamentoIngressoDTO();
        dto.setTipoIngresso("inteira");
        dto.setFormaPagamento("PIX");

        BigDecimal valor = new BigDecimal("0.01");
        configurarCenarioBasePagamento(valor);

        ingresso.setTipoIngresso("inteira");
        expectLastCall().once();
        ingresso.setValor(valor);
        expectLastCall().once();
        ingresso.setStatus(StatusIngresso.PAGO);
        expectLastCall().once();

        expect(service.salvar(ingresso)).andReturn(ingresso);

        replay(service, eventoService, usuarioService, carteiraService, participante, evento, ingresso, authentication);

        ResponseEntity response = controller.pagarIngresso(1L, dto, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(service, carteiraService, ingresso);
    }

    @Test
    void devePagarIngressoMeiaComCarteiraDebitandoMetadeDoValor() {
        PagamentoIngressoDTO dto = new PagamentoIngressoDTO();
        dto.setTipoIngresso("meia");
        dto.setFormaPagamento("CARTEIRA");

        configurarCenarioBasePagamento(new BigDecimal("0.02"));
        BigDecimal valorMeia = new BigDecimal("0.01");

        carteiraService.debitar(participante, valorMeia, ingresso);
        expectLastCall().once();

        ingresso.setTipoIngresso("meia");
        expectLastCall().once();
        ingresso.setValor(valorMeia);
        expectLastCall().once();
        ingresso.setStatus(StatusIngresso.PAGO);
        expectLastCall().once();

        expect(service.salvar(ingresso)).andReturn(ingresso);

        replay(service, eventoService, usuarioService, carteiraService, participante, evento, ingresso, authentication);

        ResponseEntity response = controller.pagarIngresso(1L, dto, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(service, carteiraService, ingresso);
    }

    @Test
    void deveRetornarBadRequestNoPagamentoQuandoDebitoDaCarteiraLancarExcecao() {
        PagamentoIngressoDTO dto = new PagamentoIngressoDTO();
        dto.setTipoIngresso("inteira");
        dto.setFormaPagamento("CARTEIRA");

        BigDecimal valor = new BigDecimal("0.01");
        configurarCenarioBasePagamento(valor);

        carteiraService.debitar(participante, valor, ingresso);
        expectLastCall().andThrow(new RegraNegocioException("Saldo insuficiente"));

        replay(service, eventoService, usuarioService, carteiraService, participante, evento, ingresso, authentication);

        ResponseEntity response = controller.pagarIngresso(1L, dto, authentication);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Saldo insuficiente", response.getBody());
        verify(carteiraService);
    }


    @Test
    void deveRetornarNotFoundAoAtualizarQuandoIngressoNaoExistir() {
        IngressoDTO dto = criarDtoValido();
        expect(service.getIngressosById(1L)).andReturn(Optional.empty());

        replay(service, eventoService, usuarioService, carteiraService, participante, evento, ingresso, authentication);

        ResponseEntity response = controller.atualizar(1L, dto);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Ingresso não encontrado", response.getBody());
        verify(service);
    }

    @Test
    void deveAtualizarIngressoERetornarStatusOkComSucesso() {
        IngressoDTO dto = criarDtoValido();

        expect(service.getIngressosById(1L)).andReturn(Optional.of(ingresso));
        expect(usuarioService.getUsuariosById(1L)).andReturn(Optional.of(participante));
        expect(eventoService.getEventosById(2L)).andReturn(Optional.of(evento));
        expect(service.salvar(anyObject(Ingresso.class))).andAnswer(() -> (Ingresso) getCurrentArguments()[0]);

        replay(service, eventoService, usuarioService, carteiraService, participante, evento, ingresso, authentication);

        ResponseEntity response = controller.atualizar(1L, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Ingresso atualizado = (Ingresso) response.getBody();
        assertNotNull(atualizado);
        assertEquals(1L, atualizado.getId());
        verify(service, usuarioService, eventoService);
    }

    @Test
    void deveRetornarBadRequestAoAtualizarQuandoRegraNegocioExceptionForLancada() {
        IngressoDTO dto = criarDtoValido();

        expect(service.getIngressosById(1L)).andReturn(Optional.of(ingresso));
        expect(usuarioService.getUsuariosById(1L)).andReturn(Optional.empty());

        replay(service, eventoService, usuarioService, carteiraService, participante, evento, ingresso, authentication);

        ResponseEntity response = controller.atualizar(1L, dto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Participante não encontrado", response.getBody());
        verify(service, usuarioService);
    }


    private IngressoDTO criarDtoValido() {
        IngressoDTO dto = new IngressoDTO();
        dto.setStatus("RESERVADO");
        dto.setTaxa(BigDecimal.ZERO);
        dto.setValor(new BigDecimal("0.01"));
        dto.setIdParticipante(1L);
        dto.setIdEvento(2L);
        return dto;
    }

    private void configurarCenarioBasePagamento(BigDecimal valorIngresso) {
        expect(authentication.getName()).andStubReturn("user@email.com");
        expect(participante.getId()).andStubReturn(1L);
        expect(evento.getId()).andStubReturn(2L);
        expect(evento.isCancelado()).andStubReturn(false);
        expect(evento.getValorIngresso()).andStubReturn(valorIngresso);

        expect(ingresso.getId()).andStubReturn(1L);
        expect(ingresso.getParticipante()).andStubReturn(participante);
        expect(ingresso.getEvento()).andStubReturn(evento);
        expect(ingresso.getStatus()).andStubReturn(StatusIngresso.RESERVADO);

        expect(service.getIngressosById(1L)).andReturn(Optional.of(ingresso));
        expect(usuarioService.buscarPorEmail("user@email.com")).andReturn(participante);
    }
}