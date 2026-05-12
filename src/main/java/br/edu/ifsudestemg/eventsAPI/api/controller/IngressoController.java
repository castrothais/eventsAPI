package br.edu.ifsudestemg.eventsAPI.api.controller;


import br.edu.ifsudestemg.eventsAPI.api.dto.IngressoDTO;
import br.edu.ifsudestemg.eventsAPI.model.entity.Ingresso;
import br.edu.ifsudestemg.eventsAPI.service.IngressoService;
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
@RequestMapping("/api/v1/ingressos")
@RequiredArgsConstructor
public class IngressoController {

    private final IngressoService service;

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
}

