package br.edu.ifsudestemg.eventsAPI.api.controller;


import br.edu.ifsudestemg.eventsAPI.api.dto.AtividadeDTO;
import br.edu.ifsudestemg.eventsAPI.api.exeption.RegraNegocioException;
import br.edu.ifsudestemg.eventsAPI.model.entity.Atividade;
import br.edu.ifsudestemg.eventsAPI.service.AtividadeService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping()
    public ResponseEntity post(AtividadeDTO dto) {
        try {
            Atividade atividade = converter(dto);
            atividade = service.salvar(atividade);
            return new ResponseEntity(atividade, HttpStatus.CREATED);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity atualizar(@PathVariable("id") Long id, AtividadeDTO dto) {
        if (!service.getAtividadesById(id).isPresent()) {
            return new ResponseEntity("Atividade não encontrada", HttpStatus.NOT_FOUND);
        }
        try {
            Atividade atividade = converter(dto);
            atividade.setId(id);
            service.salvar(atividade);
            return ResponseEntity.ok(atividade);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity excluir(@PathVariable("id") Long id) {
        Optional<Atividade> atividade = service.getAtividadesById(id);
        if (!atividade.isPresent()) {
            return new ResponseEntity("Atividade não encontrada", HttpStatus.NOT_FOUND);
        }
        try {
            service.excluir(atividade.get());
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public Atividade converter(AtividadeDTO dto) {
        ModelMapper modelMapper = new ModelMapper();
        Atividade atividade = modelMapper.map(dto, Atividade.class);
        return atividade;
    }
}
