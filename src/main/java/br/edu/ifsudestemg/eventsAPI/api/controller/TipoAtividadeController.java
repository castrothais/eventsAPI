package br.edu.ifsudestemg.eventsAPI.api.controller;


import br.edu.ifsudestemg.eventsAPI.api.dto.TipoAtividadeDTO;
import br.edu.ifsudestemg.eventsAPI.model.entity.TipoAtividade;
import br.edu.ifsudestemg.eventsAPI.service.TipoAtividadeService;
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
@RequestMapping("/api/v1/tipoAtividade")
@RequiredArgsConstructor

public class TipoAtividadeController {
    private final TipoAtividadeService service;

    @GetMapping()
    public ResponseEntity get() {
        List<TipoAtividade> TipoAtividades = service.getTipoAtividades();
        return ResponseEntity.ok(TipoAtividades.stream().map(TipoAtividadeDTO::create).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity get(@PathVariable("id") Long id) {
        Optional<TipoAtividade> TipoAtividade = service.getTipoAtividadesById(id);
        if (!TipoAtividade.isPresent()) {
            return new ResponseEntity("Tipo de atividade não encontrada", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(TipoAtividade.map(TipoAtividadeDTO::create));
    }


}
