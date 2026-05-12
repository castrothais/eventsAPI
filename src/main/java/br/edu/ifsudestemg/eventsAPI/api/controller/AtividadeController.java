package br.edu.ifsudestemg.eventsAPI.api.controller;


import br.edu.ifsudestemg.eventsAPI.api.dto.AtividadeDTO;
import br.edu.ifsudestemg.eventsAPI.model.entity.Atividade;
import br.edu.ifsudestemg.eventsAPI.service.AtividadeService;
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
@RequestMapping("/api/v1/atividades")
@RequiredArgsConstructor
public class AtividadeController {

    private final AtividadeService service;

    @GetMapping()
    public ResponseEntity get() {
        List<Atividade> atividades = service.getAtividades();
        return ResponseEntity.ok(atividades.stream().map(AtividadeDTO::create).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity get(@PathVariable("id") Long id) {
        Optional<Atividade> atividade = service.getAtividadesById(id);
        if (!atividade.isPresent()) {
            return new ResponseEntity("Atividade não encontrada", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(atividade.map(AtividadeDTO::create));
    }
}
