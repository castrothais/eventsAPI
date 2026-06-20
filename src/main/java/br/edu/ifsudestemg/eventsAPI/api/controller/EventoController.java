package br.edu.ifsudestemg.eventsAPI.api.controller;

import br.edu.ifsudestemg.eventsAPI.api.dto.EventoDTO;
import br.edu.ifsudestemg.eventsAPI.api.exeption.RegraNegocioException;
import br.edu.ifsudestemg.eventsAPI.model.entity.*;
import br.edu.ifsudestemg.eventsAPI.service.EventoService;
import br.edu.ifsudestemg.eventsAPI.service.PorteEventoService;
import br.edu.ifsudestemg.eventsAPI.service.TipoEventoService;
import br.edu.ifsudestemg.eventsAPI.service.UsuarioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/eventos")
@RequiredArgsConstructor
@Tag(name = "Evento", description = "API de Eventos")
public class
EventoController {

    private final EventoService service;
    private final UsuarioService usuarioService;
    private final TipoEventoService tipoEventoService;
    private final PorteEventoService porteEventoService;

    @GetMapping()
    @Operation(summary = "Obter eventos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Eventos encontrados"),
            @ApiResponse(responseCode = "404", description = "Nenhum evento encontrado")
    })
    public ResponseEntity get() {
        List<Evento> eventos = service.getEventos();
        return ResponseEntity.ok(eventos.stream().map(EventoDTO::create).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter informações de um evento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evento encontrado"),
            @ApiResponse(responseCode = "404", description = "Evento não encontrado")
    })
    public ResponseEntity get(@PathVariable("id") @Parameter(description = "Id do evento") Long id) {
        Optional<Evento> evento = service.getEventosById(id);
        if (!evento.isPresent()) {
            return new ResponseEntity("Evento não encontrado", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(evento.map(EventoDTO::create));
    }

    @PostMapping()
    @Operation(summary = "Salvar evento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Evento salvo com sucesso"),
            @ApiResponse(responseCode = "400", description = "Falha ao salvar evento")
    })
    public ResponseEntity post(@Valid @RequestBody EventoDTO dto) {
        try {
            Evento evento = converter(dto);
            evento = service.salvar(evento);
            return new ResponseEntity(evento, HttpStatus.CREATED);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    @Operation(summary = "Atualizar dados de evento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evento atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Falha ao atualizar evento")
    })
    public ResponseEntity atualizar( @PathVariable("id") @Parameter(description = "Id do evento")  Long id, @Valid @RequestBody EventoDTO dto) {
        if (!service.getEventosById(id).isPresent()) {
            return new ResponseEntity("Evento não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            Evento evento = converter(dto);
            evento.setId(id);
            service.salvar(evento);
            return ResponseEntity.ok(evento);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("{id}")
    @Operation(summary = "Excluir evento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Evento excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Falha ao excluir evento")
    })
    public ResponseEntity excluir( @PathVariable("id") @Parameter(description = "Id do evento") Long id) {
        Optional<Evento> evento = service.getEventosById(id);
        if (!evento.isPresent()) {
            return new ResponseEntity("Evento não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            service.excluir(evento.get());
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/upload")
    public ResponseEntity<?> uploadImagem(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {

        try {

            Evento evento = service.getEventosById(id)
                    .orElseThrow(() ->
                            new RegraNegocioException("Evento não encontrado"));

            String nomeArquivo =
                    UUID.randomUUID() + "_" + file.getOriginalFilename();

            Path pastaUpload =
                    Paths.get("uploads/eventos");

            Files.createDirectories(pastaUpload);

            Path caminho =
                    pastaUpload.resolve(nomeArquivo);

            Files.copy(file.getInputStream(), caminho);

            evento.setImagem(nomeArquivo);

            service.salvar(evento);

            return ResponseEntity.ok(nomeArquivo);

        } catch (Exception e) {

            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    @GetMapping("/imagem/{nomeArquivo}")
    public ResponseEntity<Resource> buscarImagem(
            @PathVariable String nomeArquivo) {

        try {

            Path caminho =
                    Paths.get("uploads/eventos")
                            .resolve(nomeArquivo);

            Resource resource =
                    new UrlResource(caminho.toUri());

            return ResponseEntity.ok(resource);

        } catch (Exception e) {

            return ResponseEntity.notFound().build();
        }
    }

    public Evento converter(EventoDTO dto) {

        Evento evento = new Evento();

        evento.setNome(dto.getNome());
        evento.setModalidade(Modalidade.valueOf(dto.getModalidade()));
        evento.setDataHoraInicio(dto.getDataHoraInicio());
        evento.setDataHoraFim(dto.getDataHoraFim());
        evento.setDescricao(dto.getDescricao());
        evento.setIdadeMinima(dto.getIdadeMinima());
        evento.setLotacaoMaxima(dto.getLotacaoMaxima());
        evento.setCancelado(dto.isCancelado());
        evento.setValorIngresso(dto.getValorIngresso());

        evento.setEndereco(dto.getEndereco());


        Usuario organizador = usuarioService
                .getUsuariosById(dto.getIdOrganizador())
                .orElseThrow(() -> new RegraNegocioException("Organizador não encontrado"));

        TipoEvento tipoEvento = tipoEventoService
                .getTipoEventosById(dto.getIdTipoEvento())
                .orElseThrow(() -> new RegraNegocioException("Tipo de evento não encontrado"));

        PorteEvento porteEvento = porteEventoService
                .getPorteEventosById(dto.getIdPorteEvento())
                .orElseThrow(() -> new RegraNegocioException("Porte de evento não encontrado"));

        evento.setOrganizador(organizador);
        evento.setTipoEvento(tipoEvento);
        evento.setPorteEvento(porteEvento);

        return evento;
    }
}
