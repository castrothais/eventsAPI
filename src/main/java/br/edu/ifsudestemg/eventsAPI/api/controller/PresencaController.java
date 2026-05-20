package br.edu.ifsudestemg.eventsAPI.api.controller;

import br.edu.ifsudestemg.eventsAPI.api.dto.PresencaDTO;
import br.edu.ifsudestemg.eventsAPI.api.exeption.RegraNegocioException;
import br.edu.ifsudestemg.eventsAPI.model.entity.Presenca;
import br.edu.ifsudestemg.eventsAPI.service.PresencaService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/presencas")
@RequiredArgsConstructor
public class PresencaController {

    private final PresencaService service;

    @GetMapping()
    public ResponseEntity get() {
        List<Presenca> presencas = service.getPresencas();
        return ResponseEntity.ok(presencas.stream().map(PresencaDTO::create).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity get(@PathVariable("id") Long id) {
        Optional<Presenca> presenca = service.getPresencasById(id);
        if (!presenca.isPresent()) {
            return new ResponseEntity("Presença não encontrada", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(presenca.map(PresencaDTO::create));
    }

    @PostMapping()
    public ResponseEntity post(PresencaDTO dto) {
        try {
            Presenca presenca = converter(dto);
            presenca = service.salvar(presenca);
            return new ResponseEntity(presenca, HttpStatus.CREATED);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity atualizar(@PathVariable("id") Long id, PresencaDTO dto) {
        if (!service.getPresencasById(id).isPresent()) {
            return new ResponseEntity("Presença não encontrada", HttpStatus.NOT_FOUND);
        }
        try {
            Presenca presenca = converter(dto);
            presenca.setId(id);
            service.salvar(presenca);
            return ResponseEntity.ok(presenca);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity excluir(@PathVariable("id") Long id) {
        Optional<Presenca> presenca = service.getPresencasById(id);
        if (!presenca.isPresent()) {
            return new ResponseEntity("Presença não encontrada", HttpStatus.NOT_FOUND);
        }
        try {
            service.excluir(presenca.get());
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public Presenca converter(PresencaDTO dto) {
        ModelMapper modelMapper = new ModelMapper();
        Presenca presenca = modelMapper.map(dto, Presenca.class);
        return presenca;
    }
}