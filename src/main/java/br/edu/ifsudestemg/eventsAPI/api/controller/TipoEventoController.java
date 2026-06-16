package br.edu.ifsudestemg.eventsAPI.api.controller;

import br.edu.ifsudestemg.eventsAPI.api.dto.TipoEventoDTO;
import br.edu.ifsudestemg.eventsAPI.api.exeption.RegraNegocioException;
import br.edu.ifsudestemg.eventsAPI.model.entity.TipoEvento;
import br.edu.ifsudestemg.eventsAPI.service.TipoEventoService;
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
@RequestMapping("api/v1/tipoEvento")
@RequiredArgsConstructor
@Tag(name = "Tipo de Evento", description = "API de Tipos de evento")
public class TipoEventoController {
    private final TipoEventoService service;

    @GetMapping()
    @Operation(summary = "Obter tipos de evento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tipos de evento encontradas"),
            @ApiResponse(responseCode = "404", description = "Nenhum tipo de evento encontrado")
    })
    public ResponseEntity get() {
        List<TipoEvento> tipoEventos =  service.getTipoEventos();
        return ResponseEntity.ok((tipoEventos).stream().map(TipoEventoDTO::create).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter informações de um tipo de evento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tipos de evento encontrado"),
            @ApiResponse(responseCode = "404", description = "Nenhum tipo de evento encontrado")
    })
    public ResponseEntity get(@PathVariable("id") @Parameter(description = "Id do tipo de evento") Long id) {
        Optional<TipoEvento> tipoEvento = service.getTipoEventosById(id);
        if (!tipoEvento.isPresent()) {
            return new ResponseEntity("Tipo de evento não encontrado", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(tipoEvento.map(TipoEventoDTO::create));
    }

    @PostMapping()
    @Operation(summary = "Salvar tipo de evento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tipo de evento salvo com sucesso"),
            @ApiResponse(responseCode = "400", description = "Falha ao salvar tipo de evento")
    })
    public ResponseEntity post(@Valid @RequestBody TipoEventoDTO dto) {
        try {
            TipoEvento tipoEvento = converter(dto);
            tipoEvento = service.salvar(tipoEvento);
            return new ResponseEntity(tipoEvento, HttpStatus.CREATED);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    @Operation(summary = "Atualizar tipo de evento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tipo de evento atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Falha ao salvar tipo de evento")
    })
    public ResponseEntity atualizar(@PathVariable("id") @Parameter(description = "Id do tipo de evento") Long id, @Valid @RequestBody TipoEventoDTO dto) {
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
    @Operation(summary = "Excluir tipo de evento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tipo de evento excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Falha ao excluir tipo de evento")
    })
    public ResponseEntity excluir(@PathVariable("id") @Parameter(description = "Id do tipo de evento") Long id) {
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
