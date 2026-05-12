package br.edu.ifsudestemg.eventsAPI.api.controller;

import br.edu.ifsudestemg.eventsAPI.api.dto.TipoEventoDTO;
import br.edu.ifsudestemg.eventsAPI.model.entity.TipoEvento;
import br.edu.ifsudestemg.eventsAPI.service.TipoEventoService;
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
@RequestMapping("api/v1/tipoEvento")
@RequiredArgsConstructor

public class TipoEventoController {
    private final TipoEventoService service;

    @GetMapping()
    public ResponseEntity get() {
        List<TipoEvento> tipoEventos =  service.getTipoEventos();
        return ResponseEntity.ok((tipoEventos).stream().map(TipoEventoDTO::create).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity get(@PathVariable("id") Long id) {
        Optional<TipoEvento> tipoEvento = service.getTipoEventosById(id);
        if (!tipoEvento.isPresent()) {
            return new ResponseEntity("Tipo de evento não encontrado", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(tipoEvento.map(TipoEventoDTO::create));
    }

}
