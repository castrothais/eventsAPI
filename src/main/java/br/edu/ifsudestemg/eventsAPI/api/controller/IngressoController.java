package br.edu.ifsudestemg.eventsAPI.api.controller;

import br.edu.ifsudestemg.eventsAPI.api.dto.IngressoDTO;
import br.edu.ifsudestemg.eventsAPI.api.exeption.RegraNegocioException;
import br.edu.ifsudestemg.eventsAPI.model.entity.*;
import br.edu.ifsudestemg.eventsAPI.service.EventoService;
import br.edu.ifsudestemg.eventsAPI.service.IngressoService;
import br.edu.ifsudestemg.eventsAPI.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/ingressos")
@RequiredArgsConstructor
public class IngressoController {

    private final IngressoService service;
    private final EventoService eventoService;
    private final UsuarioService usuarioService;

    @GetMapping()
    public ResponseEntity get() {
        List<Ingresso> ingressos = service.getIngressos();
        return ResponseEntity.ok(ingressos.stream().map(IngressoDTO::create).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity get(@PathVariable("id") Long id) {
        Optional<Ingresso> ingresso = service.getIngressosById(id);
        if (!ingresso.isPresent()) {
            return new ResponseEntity("Ingresso não encontrado", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(ingresso.map(IngressoDTO::create));
    }

    @PostMapping()
    public ResponseEntity post(@Valid @RequestBody IngressoDTO dto) {
        try {
            Ingresso ingresso = converter(dto);
            ingresso = service.salvar(ingresso);
            return new ResponseEntity(ingresso, HttpStatus.CREATED);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity atualizar(@PathVariable("id") Long id, @Valid @RequestBody IngressoDTO dto) {
        if (!service.getIngressosById(id).isPresent()) {
            return new ResponseEntity("Ingresso não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            Ingresso ingresso = converter(dto);
            ingresso.setId(id);
            service.salvar(ingresso);
            return ResponseEntity.ok(ingresso);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public Ingresso converter(IngressoDTO dto) {
        Ingresso ingresso = new Ingresso();
        ingresso.setStatus(StatusIngresso.valueOf(dto.getStatus()));
        ingresso.setTaxa(dto.getTaxa());
        ingresso.setValor(dto.getValor());

        Usuario participante = usuarioService.getUsuariosById(dto.getIdParticipante())
                .orElseThrow(() -> new RegraNegocioException("Participante não encontrado"));
        ingresso.setParticipante(participante);

        Evento evento = eventoService
                .getEventosById(dto.getIdEvento())
                .orElseThrow(() -> new RegraNegocioException("Evento não encontrado"));

        ingresso.setEvento(evento);

        return ingresso;
    }
}

