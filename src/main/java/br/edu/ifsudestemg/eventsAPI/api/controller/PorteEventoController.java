package br.edu.ifsudestemg.eventsAPI.api.controller;

import br.edu.ifsudestemg.eventsAPI.api.dto.PorteEventoDTO;
import br.edu.ifsudestemg.eventsAPI.api.exeption.RegraNegocioException;
import br.edu.ifsudestemg.eventsAPI.model.entity.PorteEvento;
import br.edu.ifsudestemg.eventsAPI.service.PorteEventoService;
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
@RequestMapping("/api/v1/porteEvento")
@RequiredArgsConstructor
@Tag(name = "Porte Evento", description = "API de Portes de eventos")
public class PorteEventoController {
    private final PorteEventoService service;

    @GetMapping()
    @Operation(summary = "Obter portes de evento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Portes de evento encontrados"),
            @ApiResponse(responseCode = "404", description = "Nenhum porte de evento encontrado")
    })
    public ResponseEntity get() {
        List<PorteEvento> PorteEventos = service.getPorteEventos();
        return ResponseEntity.ok(PorteEventos.stream().map(PorteEventoDTO::create).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter informações de um porte de evento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Porte de evento encontrado"),
            @ApiResponse(responseCode = "404", description = "Porte de evento não encontrado")
    })
    public ResponseEntity get( @PathVariable("id") @Parameter(description = "Id do porte de evento") Long id) {
        Optional<PorteEvento> PorteEvento = service.getPorteEventosById(id);
        if (!PorteEvento.isPresent()) {
            return new ResponseEntity("Porte de evento não encontrado", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(PorteEvento.map(PorteEventoDTO::create));
    }

    @PostMapping()
    @Operation(summary = "Salvar porte de evento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Porte de evento salvo com sucesso"),
            @ApiResponse(responseCode = "400", description = "Falha ao salvar porte de evento")
    })
    public ResponseEntity post(@Valid @RequestBody PorteEventoDTO dto) {
        try {
            PorteEvento porteEvento = converter(dto);
            porteEvento = service.salvar(porteEvento);
            return new ResponseEntity(porteEvento, HttpStatus.CREATED);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    @Operation(summary = "Atualizar dados de porte de evento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Porte de evento atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Falha ao atualizar porte de evento")
    })
    public ResponseEntity atualizar(@PathVariable("id") @Parameter(description = "Id do porte de evento") Long id, @Valid @RequestBody PorteEventoDTO dto) {
        if (!service.getPorteEventosById(id).isPresent()) {
            return new ResponseEntity("Porte de evento não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            PorteEvento porteEvento = converter(dto);
            porteEvento.setId(id);
            service.salvar(porteEvento);
            return ResponseEntity.ok(porteEvento);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("{id}")
    @Operation(summary = "Excluir porte de evento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Porte de evento excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Falha ao excluir porte de evento")
    })
    public ResponseEntity excluir(@PathVariable("id") @Parameter(description = "Id do porte de evento") Long id) {
        Optional<PorteEvento> porteEvento = service.getPorteEventosById(id);
        if (!porteEvento.isPresent()) {
            return new ResponseEntity("Porte de evento não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            service.excluir(porteEvento.get());
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public PorteEvento converter(PorteEventoDTO dto) {
        ModelMapper modelMapper = new ModelMapper();
        PorteEvento porteEvento = modelMapper.map(dto, PorteEvento.class);
        return porteEvento;
    }
}
