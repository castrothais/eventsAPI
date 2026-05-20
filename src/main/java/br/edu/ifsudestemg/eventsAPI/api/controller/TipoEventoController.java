package br.edu.ifsudestemg.eventsAPI.api.controller;

import br.edu.ifsudestemg.eventsAPI.api.dto.TipoEventoDTO;
import br.edu.ifsudestemg.eventsAPI.api.exeption.RegraNegocioException;
import br.edu.ifsudestemg.eventsAPI.model.entity.TipoEvento;
import br.edu.ifsudestemg.eventsAPI.service.TipoEventoService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping()
    public ResponseEntity post(TipoEventoDTO dto) {
        try {
            TipoEvento tipoEvento = converter(dto);
            tipoEvento = service.salvar(tipoEvento);
            return new ResponseEntity(tipoEvento, HttpStatus.CREATED);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity atualizar(@PathVariable("id") Long id, TipoEventoDTO dto) {
        if (!service.getTipoEventosById(id).isPresent()) {
            return new ResponseEntity("Tipo evento não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            TipoEvento tipoEvento = converter(dto);
            tipoEvento.setId(id);
            service.salvar(tipoEvento);
            return ResponseEntity.ok(tipoEvento);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity excluir(@PathVariable("id") Long id) {
        Optional<TipoEvento> tipoEvento = service.getTipoEventosById(id);
        if (!tipoEvento.isPresent()) {
            return new ResponseEntity("Tipo evento não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            service.excluir(tipoEvento.get());
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public TipoEvento converter(TipoEventoDTO dto) {
        ModelMapper modelMapper = new ModelMapper();
        TipoEvento tipoEvento = modelMapper.map(dto, TipoEvento.class);
        return tipoEvento;
    }
}
