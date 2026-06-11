package br.edu.ifsudestemg.eventsAPI.api.controller;


import br.edu.ifsudestemg.eventsAPI.api.dto.TipoAtividadeDTO;
import br.edu.ifsudestemg.eventsAPI.api.exeption.RegraNegocioException;
import br.edu.ifsudestemg.eventsAPI.model.entity.TipoAtividade;
import br.edu.ifsudestemg.eventsAPI.service.TipoAtividadeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@Tag(name = "Tipo de Atividade", description = "API de Tipos de atividade")
public class TipoAtividadeController {
    private final TipoAtividadeService service;

    @GetMapping()
    @Operation(summary = "Obter tipos de atividade")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tipos de atividade encontrados"),
            @ApiResponse(responseCode = "404", description = "Nenhum tipo de atividade encontrado")
    })
    public ResponseEntity get() {
        List<TipoAtividade> TipoAtividades = service.getTipoAtividades();
        return ResponseEntity.ok(TipoAtividades.stream().map(TipoAtividadeDTO::create).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter informações de um tipo de atividade")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tipos de atividade encontrado"),
            @ApiResponse(responseCode = "404", description = "Nenhum tipo de atividade encontrado")
    })
    public ResponseEntity get(@PathVariable("id") @Parameter(description = "Id do tipo de atividade") Long id) {
        Optional<TipoAtividade> TipoAtividade = service.getTipoAtividadesById(id);
        if (!TipoAtividade.isPresent()) {
            return new ResponseEntity("Tipo de atividade não encontrada", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(TipoAtividade.map(TipoAtividadeDTO::create));
    }

    @PostMapping()
    @Operation(summary = "Salvar tipo de atividade")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tipo de atividade salvo com sucesso"),
            @ApiResponse(responseCode = "400", description = "Falha ao salvar tipo de atividade")
    })
    public ResponseEntity post(@Valid @RequestBody TipoAtividadeDTO dto) {
        try {
            TipoAtividade tipoAtividade = converter(dto);
            tipoAtividade = service.salvar(tipoAtividade);
            return new ResponseEntity(tipoAtividade, HttpStatus.CREATED);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    @Operation(summary = "Atualizar tipo de atividade")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tipo de atividade atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Falha ao atualizar tipo de atividade")
    })
    public ResponseEntity atualizar(@PathVariable("id") @Parameter(description = "Id do tipo de atividade") Long id,@Valid @RequestBody TipoAtividadeDTO dto) {
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
    @Operation(summary = "Excluir ipo de atividade")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tipo de atividade excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Falha ao excluir tipo de atividade")
    })
    public ResponseEntity excluir(@PathVariable("id") @Parameter(description = "Id do tipo de atividade") Long id) {
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
