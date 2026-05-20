package br.edu.ifsudestemg.eventsAPI.api.controller;

import br.edu.ifsudestemg.eventsAPI.api.dto.EventoDTO;
import br.edu.ifsudestemg.eventsAPI.api.exeption.RegraNegocioException;
import br.edu.ifsudestemg.eventsAPI.model.entity.*;
import br.edu.ifsudestemg.eventsAPI.service.EventoService;
import br.edu.ifsudestemg.eventsAPI.service.PorteEventoService;
import br.edu.ifsudestemg.eventsAPI.service.TipoEventoService;
import br.edu.ifsudestemg.eventsAPI.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/eventos")
@RequiredArgsConstructor
public class EventoController {

    private final EventoService service;
    private final UsuarioService usuarioService;
    private final TipoEventoService tipoEventoService;
    private final PorteEventoService porteEventoService;

    @GetMapping()
    public ResponseEntity get() {
        List<Evento> eventos = service.getEventos();
        return ResponseEntity.ok(eventos.stream().map(EventoDTO::create).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity get(@PathVariable("id") Long id) {
        Optional<Evento> evento = service.getEventosById(id);
        if (!evento.isPresent()) {
            return new ResponseEntity("Evento não encontrado", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(evento.map(EventoDTO::create));
    }

    @PostMapping()
    public ResponseEntity post(@Valid @RequestBody EventoDTO dto) {
        try {
            Evento evento = converter(dto);
            evento = service.salvar(evento);
            return new ResponseEntity(evento, HttpStatus.CREATED);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity atualizar(@PathVariable("id") Long id,@Valid @RequestBody EventoDTO dto) {
        if (!service.getEventosById(id).isPresent()) {
            return new ResponseEntity("Evento não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            Evento evento = converter(dto);
            evento.setId(id);
            service.salvar(evento);
            return ResponseEntity.ok(evento);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity excluir(@PathVariable("id") Long id) {
        Optional<Evento> evento = service.getEventosById(id);
        if (!evento.isPresent()) {
            return new ResponseEntity("Evento não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            service.excluir(evento.get());
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public Evento converter(EventoDTO dto) {

        Evento evento = new Evento();

        evento.setNome(dto.getNome());
        evento.setModalidade(Modalidade.valueOf(dto.getModalidade()));
        evento.setDataHoraInicio(dto.getDataHoraInicio());
        evento.setDataHoraFim(dto.getDataHoraFim());
        evento.setDescricao(dto.getDescricao());
        evento.setIdadeMinima(dto.getIdadeMinima());
        evento.setLotacaoMaxima(dto.getLotacaoMaxima());
        evento.setCancelado(dto.isCancelado());

        evento.setEndereco(dto.getEndereco());


        Usuario organizador = usuarioService
                .getUsuariosById(dto.getIdOrganizador())
                .orElseThrow(() -> new RegraNegocioException("Organizador não encontrado"));

        TipoEvento tipoEvento = tipoEventoService
                .getTipoEventosById(dto.getIdTipoEvento())
                .orElseThrow(() -> new RegraNegocioException("Tipo de evento não encontrado"));

        PorteEvento porteEvento = porteEventoService
                .getPorteEventosById(dto.getIdPorteEvento())
                .orElseThrow(() -> new RegraNegocioException("Porte de evento não encontrado"));

        evento.setOrganizador(organizador);
        evento.setTipoEvento(tipoEvento);
        evento.setPorteEvento(porteEvento);

        return evento;
    }
}
