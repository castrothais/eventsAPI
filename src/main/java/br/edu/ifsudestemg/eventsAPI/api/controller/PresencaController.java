package br.edu.ifsudestemg.eventsAPI.api.controller;

import br.edu.ifsudestemg.eventsAPI.api.dto.PresencaDTO;
import br.edu.ifsudestemg.eventsAPI.model.entity.Presenca;
import br.edu.ifsudestemg.eventsAPI.service.PresencaService;
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
}