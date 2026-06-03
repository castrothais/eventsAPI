package br.edu.ifsudestemg.eventsAPI.api.controller;

import br.edu.ifsudestemg.eventsAPI.api.dto.PresencaDTO;
import br.edu.ifsudestemg.eventsAPI.api.exeption.RegraNegocioException;
import br.edu.ifsudestemg.eventsAPI.model.entity.Presenca;
import br.edu.ifsudestemg.eventsAPI.service.PresencaService;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/api/v1/presencas")
@RequiredArgsConstructor
@Tag(name = "Presença", description = "API de Presenças")
public class PresencaController {

    private final PresencaService service;

    @GetMapping()
    @Operation(summary = "Obter presenças")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Presenças encontradas"),
            @ApiResponse(responseCode = "404", description = "Nenhuma presença encontrada")
    })
    public ResponseEntity get() {
        List<Presenca> presencas = service.getPresencas();
        return ResponseEntity.ok(presencas.stream().map(PresencaDTO::create).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter informações de uma presença")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Presença encontrada"),
            @ApiResponse(responseCode = "404", description = "Presença não encontrada")
    })
    public ResponseEntity get(@PathVariable("id") @Parameter(description = "Id da presença") Long id) {
        Optional<Presenca> presenca = service.getPresencasById(id);
        if (!presenca.isPresent()) {
            return new ResponseEntity("Presença não encontrada", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(presenca.map(PresencaDTO::create));
    }

    @PostMapping()
    @Operation(summary = "Salvar presença")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Presença salva com sucesso"),
            @ApiResponse(responseCode = "400", description = "Falha ao salvar presença")
    })
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
    @Operation(summary = "Atualizar presença")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Presença atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Falha ao atualizar presença")
    })
    public ResponseEntity atualizar(@PathVariable("id") @Parameter(description = "Id da presença") Long id, PresencaDTO dto) {
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
    @Operation(summary = "Obter informações de uma presença")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Presença excluída com sucesso"),
            @ApiResponse(responseCode = "404", description = "Falha ao excluir presença")
    })
    public ResponseEntity excluir(@PathVariable("id") @Parameter(description = "Id da presença") Long id) {
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