package br.edu.ifsudestemg.eventsAPI.api.controller;

import br.edu.ifsudestemg.eventsAPI.api.dto.PorteEventoDTO;
import br.edu.ifsudestemg.eventsAPI.model.entity.PorteEvento;
import br.edu.ifsudestemg.eventsAPI.service.PorteEventoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/porteEvento")
@RequiredArgsConstructor

public class PorteEventoController {
    private final PorteEventoService service;

    @GetMapping()
    public ResponseEntity get() {
        List<PorteEvento> PorteEventos = service.getPorteEventos();
        return ResponseEntity.ok(PorteEventos.stream().map(PorteEventoDTO::create).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity get(@PathVariable("id") Long id) {
        Optional<PorteEvento> PorteEvento = service.getPorteEventosById(id);
        if (!PorteEvento.isPresent()) {
            return new ResponseEntity("Porte de evento não encontrado", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(PorteEvento.map(PorteEventoDTO::create));
    }
}
