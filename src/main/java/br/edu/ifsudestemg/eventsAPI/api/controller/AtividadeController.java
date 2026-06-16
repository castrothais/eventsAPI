package br.edu.ifsudestemg.eventsAPI.api.controller;


import br.edu.ifsudestemg.eventsAPI.api.dto.AtividadeDTO;
import br.edu.ifsudestemg.eventsAPI.api.exeption.RegraNegocioException;
import br.edu.ifsudestemg.eventsAPI.model.entity.Atividade;
import br.edu.ifsudestemg.eventsAPI.model.entity.Evento;
import br.edu.ifsudestemg.eventsAPI.model.entity.TipoAtividade;
import br.edu.ifsudestemg.eventsAPI.model.entity.Usuario;
import br.edu.ifsudestemg.eventsAPI.service.AtividadeService;
import br.edu.ifsudestemg.eventsAPI.service.EventoService;
import br.edu.ifsudestemg.eventsAPI.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/atividades")
@RequiredArgsConstructor
@Tag(name = "Atividade", description = "API de Atividades de evento")
public class AtividadeController {

    private final AtividadeService service;
    private final UsuarioService usuarioService;
    private final EventoService eventoService;

    @GetMapping()
    @Operation(summary = "Obter atividades")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Atividades encontradas"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Nenhuma atividade encontrada"
            )
    })
    public ResponseEntity get() {
        List<Atividade> atividades = service.getAtividades();
        return ResponseEntity.ok(atividades.stream().map(AtividadeDTO::create).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter informações de uma atividade")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Atividade encontrada"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Atividade não encontrada"
            )
    })
    public ResponseEntity get(@PathVariable("id") @Parameter(description = "Id da atividade") Long id) {
        Optional<Atividade> atividade = service.getAtividadesById(id);
        if (!atividade.isPresent()) {
            return new ResponseEntity("Atividade não encontrada", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(atividade.map(AtividadeDTO::create));
    }

    @PostMapping()
    @Operation(summary = "Salvar atividade")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Atividade salva com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Falha ao salvar atividade"
            )
    })
    public ResponseEntity post(@Valid @RequestBody AtividadeDTO dto) {
        try {
            Atividade atividade = converter(dto);
            atividade = service.salvar(atividade);
            return new ResponseEntity(atividade, HttpStatus.CREATED);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    @Operation(summary = "Atualizar dados de atividade")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Atividade atualizada com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Falha ao atualizar atividade"
            )
    })
    public ResponseEntity atualizar(@PathVariable("id") @Parameter(description = "Id da atividade") Long id, @Valid @RequestBody AtividadeDTO dto) {
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
    @Operation(summary = "Excluir atividade")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Atividade excluída com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Falha ao excluir atividade"
            )
    })
    public ResponseEntity excluir(@PathVariable("id") @Parameter(description = "Id da atividade") Long id) {
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
        Atividade atividade = new Atividade();

        atividade.setNome(dto.getNome());
        atividade.setCancelado(dto.isCancelado());

        Evento evento = eventoService
                .getEventosById(dto.getIdEvento())
                .orElseThrow(() -> new RegraNegocioException("Evento não encontrado"));

        atividade.setEvento(evento);

        Usuario ministrante = usuarioService.getUsuariosById(dto.getIdMinistrante())
                .orElseThrow(() -> new RegraNegocioException("Ministrante não encontrado"));

        atividade.setMinistrante(ministrante);

        TipoAtividade tipo = new TipoAtividade();
        tipo.setId(dto.getIdTipoAtividade());
        atividade.setTipoAtividade(tipo);

        return atividade;
    }
}
