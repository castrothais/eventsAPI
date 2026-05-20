package br.edu.ifsudestemg.eventsAPI.api.controller;


import br.edu.ifsudestemg.eventsAPI.api.dto.TipoAtividadeDTO;
import br.edu.ifsudestemg.eventsAPI.api.exeption.RegraNegocioException;
import br.edu.ifsudestemg.eventsAPI.model.entity.TipoAtividade;
import br.edu.ifsudestemg.eventsAPI.service.TipoAtividadeService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping()
    public ResponseEntity post(TipoAtividadeDTO dto) {
        try {
            TipoAtividade tipoAtividade = converter(dto);
            tipoAtividade = service.salvar(tipoAtividade);
            return new ResponseEntity(tipoAtividade, HttpStatus.CREATED);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity atualizar(@PathVariable("id") Long id, TipoAtividadeDTO dto) {
        if (!service.getTipoAtividadesById(id).isPresent()) {
            return new ResponseEntity("Tipo atividade não encontrada", HttpStatus.NOT_FOUND);
        }
        try {
            TipoAtividade tipoAtividade = converter(dto);
            tipoAtividade.setId(id);
            service.salvar(tipoAtividade);
            return ResponseEntity.ok(tipoAtividade);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity excluir(@PathVariable("id") Long id) {
        Optional<TipoAtividade> tipoAtividade = service.getTipoAtividadesById(id);
        if (!tipoAtividade.isPresent()) {
            return new ResponseEntity("Tipo atividade não encontrada", HttpStatus.NOT_FOUND);
        }
        try {
            service.excluir(tipoAtividade.get());
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public TipoAtividade converter(TipoAtividadeDTO dto) {
        ModelMapper modelMapper = new ModelMapper();
        TipoAtividade tipoAtividade = modelMapper.map(dto, TipoAtividade.class);
        return tipoAtividade;
    }
}
