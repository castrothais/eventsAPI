package br.edu.ifsudestemg.eventsAPI.service;

import br.edu.ifsudestemg.eventsAPI.api.exeption.RegraNegocioException;
import br.edu.ifsudestemg.eventsAPI.model.entity.*;
import br.edu.ifsudestemg.eventsAPI.model.repository.EventoRepository;
import br.edu.ifsudestemg.eventsAPI.model.repository.IngressoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.*;

class EventoServiceTest {

    private EventoRepository repository;
    private IngressoRepository ingressoRepository;
    private CarteiraService carteiraService;
    private EventoService service;

    private Evento evento;
    private PorteEvento porteEvento;

    @BeforeEach
    void setUp() {
        repository = createMock(EventoRepository.class);
        ingressoRepository = createMock(IngressoRepository.class);
        carteiraService = createMock(CarteiraService.class);
        service = new EventoService(repository, ingressoRepository, carteiraService);

        evento = createNiceMock(Evento.class);
        porteEvento = createNiceMock(PorteEvento.class);
    }


    @Test
    void deveRetornarListaDeEventos() {
        expect(repository.findAll()).andReturn(List.of(evento));
        replay(repository, ingressoRepository, carteiraService, evento, porteEvento);

        List<Evento> resultado = service.getEventos();

        assertEquals(1, resultado.size());
        verify(repository);
    }

    @Test
    void deveRetornarEventoPorIdQuandoExistir() {
        expect(repository.findById(1L)).andReturn(Optional.of(evento));
        replay(repository, ingressoRepository, carteiraService, evento, porteEvento);

        Optional<Evento> resultado = service.getEventosById(1L);

        assertTrue(resultado.isPresent());
        verify(repository);
    }

    @Test
    void deveSalvarNovoEventoNosLimiaresMinimosComSucesso() {
        LocalDateTime inicio = LocalDateTime.now().plusMinutes(1);
        LocalDateTime fim = inicio;

        expect(evento.getId()).andStubReturn(null);
        expect(evento.getNome()).andStubReturn("a");
        expect(evento.getDataHoraInicio()).andStubReturn(inicio);
        expect(evento.getDataHoraFim()).andStubReturn(fim);
        expect(evento.getLotacaoMaxima()).andStubReturn(1);
        expect(evento.getPorteEvento()).andStubReturn(porteEvento);

        expect(porteEvento.getMinParticipantes()).andStubReturn(1);
        expect(porteEvento.getMaxParticipantes()).andStubReturn(100);

        expect(repository.existsByNomeIgnoreCase("a")).andReturn(false);
        expect(repository.save(evento)).andReturn(evento);

        replay(repository, ingressoRepository, carteiraService, evento, porteEvento);

        Evento salvo = service.salvar(evento);

        assertNotNull(salvo);
        verify(repository);
    }

    @Test
    void deveSalvarEdicaoDeEventoNoLimiarMaximoDeAntecedenciaELotacao() {
        LocalDateTime inicio = LocalDateTime.now().plusYears(2).withMonth(12).withDayOfMonth(31);
        LocalDateTime fim = inicio.plusHours(1);

        expect(evento.getId()).andStubReturn(1L);
        expect(evento.getNome()).andStubReturn("Evento Existente");
        expect(evento.getDataHoraInicio()).andStubReturn(inicio);
        expect(evento.getDataHoraFim()).andStubReturn(fim);
        expect(evento.getLotacaoMaxima()).andStubReturn(100);
        expect(evento.getPorteEvento()).andStubReturn(porteEvento);

        expect(porteEvento.getMinParticipantes()).andStubReturn(1);
        expect(porteEvento.getMaxParticipantes()).andStubReturn(100);

        expect(repository.save(evento)).andReturn(evento);

        replay(repository, ingressoRepository, carteiraService, evento, porteEvento);

        Evento salvo = service.salvar(evento);

        assertNotNull(salvo);
        verify(repository);
    }

    @Test
    void deveLancarExcecaoAoExcluirQuandoEventoForNulo() {
        replay(repository, ingressoRepository, carteiraService, evento, porteEvento);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> service.excluir(null));
        assertEquals("Evento não encontrado.", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoAoExcluirQuandoIdDoEventoForNulo() {
        expect(evento.getId()).andStubReturn(null);
        replay(repository, ingressoRepository, carteiraService, evento, porteEvento);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> service.excluir(evento));
        assertEquals("Evento não encontrado.", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoAoExcluirQuandoEventoNaoExistirNoRepositorio() {
        expect(evento.getId()).andStubReturn(1L);
        expect(repository.findById(1L)).andReturn(Optional.empty());

        replay(repository, ingressoRepository, carteiraService, evento, porteEvento);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> service.excluir(evento));
        assertEquals("Evento não encontrado.", ex.getMessage());
        verify(repository);
    }

    @Test
    void deveExcluirECancelarEventoExistenteComSucesso() {
        expect(evento.getId()).andStubReturn(1L);
        expect(evento.isCancelado()).andStubReturn(false);
        expect(evento.getDataHoraInicio()).andStubReturn(LocalDateTime.now().plusHours(25));
        expect(evento.getPorteEvento()).andStubReturn(porteEvento);
        expect(porteEvento.getTempoMinimoCancelamento()).andStubReturn(24);

        evento.setCancelado(true);
        expectLastCall().once();

        expect(repository.findById(1L)).andReturn(Optional.of(evento));
        expect(ingressoRepository.findByEvento_Id(1L)).andReturn(Collections.emptyList());
        expect(repository.save(evento)).andReturn(evento);

        replay(repository, ingressoRepository, carteiraService, evento, porteEvento);

        service.excluir(evento);

        verify(repository, ingressoRepository, evento);
    }

    @Test
    void deveLancarExcecaoQuandoNomeForVazio() {
        expect(evento.getNome()).andStubReturn("   ");
        replay(repository, ingressoRepository, carteiraService, evento, porteEvento);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> service.validar(evento));
        assertEquals("Nome não pode ser vazio", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoNomeNaoContiverLetrasValidas() {
        expect(evento.getNome()).andStubReturn("12345!");
        replay(repository, ingressoRepository, carteiraService, evento, porteEvento);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> service.validar(evento));
        assertEquals("Nome deve conter letras válidas", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoNovoEventoJaExistirComMesmoNome() {
        expect(evento.getId()).andStubReturn(null);
        expect(evento.getNome()).andStubReturn("Teste");
        expect(repository.existsByNomeIgnoreCase("Teste")).andReturn(true);

        replay(repository, ingressoRepository, carteiraService, evento, porteEvento);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> service.validar(evento));
        assertEquals("Já existe uma evento com esse nome", ex.getMessage());
        verify(repository);
    }

    @Test
    void deveLancarExcecaoQuandoDataFimForUmSegundoAntesDaDataInicio() {
        LocalDateTime inicio = LocalDateTime.now().plusDays(1);
        LocalDateTime fim = inicio.minusSeconds(1);

        expect(evento.getId()).andStubReturn(1L);
        expect(evento.getNome()).andStubReturn("Teste");
        expect(evento.getDataHoraInicio()).andStubReturn(inicio);
        expect(evento.getDataHoraFim()).andStubReturn(fim);

        replay(repository, ingressoRepository, carteiraService, evento, porteEvento);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> service.validar(evento));
        assertEquals("Data fim deve ser após a data início", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoAnoInicioExcederDoisAnosDeAntecedenciaPorUmAno() {
        LocalDateTime inicio = LocalDateTime.now().plusYears(3);
        LocalDateTime fim = inicio.plusHours(1);

        expect(evento.getId()).andStubReturn(1L);
        expect(evento.getNome()).andStubReturn("Teste");
        expect(evento.getDataHoraInicio()).andStubReturn(inicio);
        expect(evento.getDataHoraFim()).andStubReturn(fim);

        replay(repository, ingressoRepository, carteiraService, evento, porteEvento);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> service.validar(evento));
        assertEquals("Só é possível marcar um evento com até 2 anos de antecedência", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoDataInicioForUmSegundoAntesDaDataAtual() {
        LocalDateTime inicio = LocalDateTime.now().minusSeconds(1);
        LocalDateTime fim = LocalDateTime.now().plusHours(1);

        expect(evento.getId()).andStubReturn(1L);
        expect(evento.getNome()).andStubReturn("Teste");
        expect(evento.getDataHoraInicio()).andStubReturn(inicio);
        expect(evento.getDataHoraFim()).andStubReturn(fim);

        replay(repository, ingressoRepository, carteiraService, evento, porteEvento);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> service.validar(evento));
        assertEquals("Não é possível criar um evento antes da data atual", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoNaSegundaCondicaoDoOrQuandoDataFimForAnteriorADataAtual() {
        LocalDateTime inicioFuturo = LocalDateTime.now().plusHours(2);
        LocalDateTime fimPassado = LocalDateTime.now().minusSeconds(1);

        expect(evento.getId()).andStubReturn(1L);
        expect(evento.getNome()).andStubReturn("Teste");
        expect(evento.getDataHoraInicio()).andStubReturn(inicioFuturo);
        expect(evento.getDataHoraFim()).andReturn(inicioFuturo.plusHours(1));
        expect(evento.getDataHoraFim()).andReturn(fimPassado);

        replay(repository, ingressoRepository, carteiraService, evento, porteEvento);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> service.validar(evento));
        assertEquals("Não é possível criar um evento antes da data atual", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoEventoJaEstiverFinalizadoNaVerificacaoDeEdicao() {
        LocalDateTime futuro = LocalDateTime.now().plusHours(2);
        LocalDateTime passado = LocalDateTime.now().minusSeconds(1);

        expect(evento.getId()).andStubReturn(1L);
        expect(evento.getNome()).andStubReturn("Teste");
        expect(evento.getDataHoraInicio()).andStubReturn(futuro);
        expect(evento.getDataHoraFim()).andReturn(futuro.plusHours(1)).times(2);
        expect(evento.getDataHoraFim()).andReturn(passado);

        replay(repository, ingressoRepository, carteiraService, evento, porteEvento);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> service.validar(evento));
        assertEquals("Edição não permitida, evento finalizado", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoLotacaoMaximaExcederMaximoDoPortePorUm() {
        LocalDateTime inicio = LocalDateTime.now().plusDays(1);
        LocalDateTime fim = inicio.plusHours(2);

        expect(evento.getId()).andStubReturn(1L);
        expect(evento.getNome()).andStubReturn("Teste");
        expect(evento.getDataHoraInicio()).andStubReturn(inicio);
        expect(evento.getDataHoraFim()).andStubReturn(fim);
        expect(evento.getLotacaoMaxima()).andStubReturn(101);
        expect(evento.getPorteEvento()).andStubReturn(porteEvento);
        expect(porteEvento.getMaxParticipantes()).andStubReturn(100);

        replay(repository, ingressoRepository, carteiraService, evento, porteEvento);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> service.validar(evento));
        assertEquals("Lotação acima do máximo do porte", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoLotacaoMaximaForInferiorAoMinimoDoPortePorUm() {
        LocalDateTime inicio = LocalDateTime.now().plusDays(1);
        LocalDateTime fim = inicio.plusHours(2);

        expect(evento.getId()).andStubReturn(1L);
        expect(evento.getNome()).andStubReturn("Teste");
        expect(evento.getDataHoraInicio()).andStubReturn(inicio);
        expect(evento.getDataHoraFim()).andStubReturn(fim);
        expect(evento.getLotacaoMaxima()).andStubReturn(9);
        expect(evento.getPorteEvento()).andStubReturn(porteEvento);
        expect(porteEvento.getMaxParticipantes()).andStubReturn(100);
        expect(porteEvento.getMinParticipantes()).andStubReturn(10);

        replay(repository, ingressoRepository, carteiraService, evento, porteEvento);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> service.validar(evento));
        assertEquals("Lotação abaixo do mínimo do porte", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoAoCancelarQuandoEventoForNulo() {
        replay(repository, ingressoRepository, carteiraService, evento, porteEvento);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> service.cancelarEvento(null));
        assertEquals("Evento não encontrado.", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoAoCancelarQuandoIdDoEventoForNulo() {
        expect(evento.getId()).andStubReturn(null);
        replay(repository, ingressoRepository, carteiraService, evento, porteEvento);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> service.cancelarEvento(evento));
        assertEquals("Evento não encontrado.", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoAoCancelarQuandoEventoJaEstiverCancelado() {
        expect(evento.getId()).andStubReturn(1L);
        expect(evento.isCancelado()).andStubReturn(true);
        replay(repository, ingressoRepository, carteiraService, evento, porteEvento);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> service.cancelarEvento(evento));
        assertEquals("Este evento já foi cancelado.", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoAoCancelarQuandoFaltarMenosQueTempoMinimoPorUmaHora() {
        expect(evento.getId()).andStubReturn(1L);
        expect(evento.isCancelado()).andStubReturn(false);
        expect(evento.getDataHoraInicio()).andStubReturn(LocalDateTime.now().plusHours(23).plusMinutes(30));
        expect(evento.getPorteEvento()).andStubReturn(porteEvento);
        expect(porteEvento.getTempoMinimoCancelamento()).andStubReturn(24);

        replay(repository, ingressoRepository, carteiraService, evento, porteEvento);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> service.cancelarEvento(evento));
        assertEquals("Cancelamento não permitido. O prazo mínimo é de 24 horas.", ex.getMessage());
    }

    @Test
    void deveCancelarEventoNoLimiarExatoDeHorasEProcessarIngressosPagosReservadosEOutros() {
        Usuario participante = createNiceMock(Usuario.class);
        Ingresso ingressoPago = createNiceMock(Ingresso.class);
        Ingresso ingressoReservado = createNiceMock(Ingresso.class);
        Ingresso ingressoCancelado = createNiceMock(Ingresso.class);

        BigDecimal valorIngresso = new BigDecimal("0.01");

        expect(evento.getId()).andStubReturn(1L);
        expect(evento.isCancelado()).andStubReturn(false);
        expect(evento.getDataHoraInicio()).andStubReturn(LocalDateTime.now().plusHours(24).plusMinutes(1));
        expect(evento.getPorteEvento()).andStubReturn(porteEvento);
        expect(porteEvento.getTempoMinimoCancelamento()).andStubReturn(24);

        expect(ingressoPago.getStatus()).andStubReturn(StatusIngresso.PAGO);
        expect(ingressoPago.getParticipante()).andStubReturn(participante);
        expect(ingressoPago.getValor()).andStubReturn(valorIngresso);
        ingressoPago.setStatus(StatusIngresso.CANCELADO);
        expectLastCall().once();

        expect(ingressoReservado.getStatus()).andStubReturn(StatusIngresso.RESERVADO);
        ingressoReservado.setStatus(StatusIngresso.CANCELADO);
        expectLastCall().once();

        expect(ingressoCancelado.getStatus()).andStubReturn(StatusIngresso.CANCELADO);

        carteiraService.creditar(participante, valorIngresso, ingressoPago);
        expectLastCall().once();

        evento.setCancelado(true);
        expectLastCall().once();

        expect(ingressoRepository.findByEvento_Id(1L))
                .andReturn(List.of(ingressoPago, ingressoReservado, ingressoCancelado));
        expect(repository.save(evento)).andReturn(evento);

        replay(repository, ingressoRepository, carteiraService, evento, porteEvento,
                participante, ingressoPago, ingressoReservado, ingressoCancelado);

        service.cancelarEvento(evento);

        verify(repository, ingressoRepository, carteiraService, evento, ingressoPago, ingressoReservado);
    }
}