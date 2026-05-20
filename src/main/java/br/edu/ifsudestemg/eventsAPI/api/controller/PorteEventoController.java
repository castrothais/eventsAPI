package br.edu.ifsudestemg.eventsAPI.api.controller;

import br.edu.ifsudestemg.eventsAPI.api.dto.PorteEventoDTO;
import br.edu.ifsudestemg.eventsAPI.api.exeption.RegraNegocioException;
import br.edu.ifsudestemg.eventsAPI.model.entity.PorteEvento;
import br.edu.ifsudestemg.eventsAPI.service.PorteEventoService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping()
    public ResponseEntity post(PorteEventoDTO dto) {
        try {
            PorteEvento porteEvento = converter(dto);
            porteEvento = service.salvar(porteEvento);
            return new ResponseEntity(porteEvento, HttpStatus.CREATED);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity atualizar(@PathVariable("id") Long id, PorteEventoDTO dto) {
        if (!service.getPorteEventosById(id).isPresent()) {
            return new ResponseEntity("Porte de evento não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            PorteEvento porteEvento = converter(dto);
            porteEvento.setId(id);
            service.salvar(porteEvento);
            return ResponseEntity.ok(porteEvento);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity excluir(@PathVariable("id") Long id) {
        Optional<PorteEvento> porteEvento = service.getPorteEventosById(id);
        if (!porteEvento.isPresent()) {
            return new ResponseEntity("Porte de evento não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            service.excluir(porteEvento.get());
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public PorteEvento converter(PorteEventoDTO dto) {
        ModelMapper modelMapper = new ModelMapper();
        PorteEvento porteEvento = modelMapper.map(dto, PorteEvento.class);
        return porteEvento;
    }
}
