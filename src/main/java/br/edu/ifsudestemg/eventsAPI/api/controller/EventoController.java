package br.edu.ifsudestemg.eventsAPI.api.controller;

import br.edu.ifsudestemg.eventsAPI.api.dto.EventoDTO;
import br.edu.ifsudestemg.eventsAPI.api.exeption.RegraNegocioException;
import br.edu.ifsudestemg.eventsAPI.model.entity.Evento;
import br.edu.ifsudestemg.eventsAPI.service.EventoService;
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
    public ResponseEntity post(EventoDTO dto) {
        try {
            Evento evento = converter(dto);
            evento = service.salvar(evento);
            return new ResponseEntity(evento, HttpStatus.CREATED);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity atualizar(@PathVariable("id") Long id, EventoDTO dto) {
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
        ModelMapper modelMapper = new ModelMapper();
        Evento evento = modelMapper.map(dto, Evento.class);
        return evento;
    }
}
