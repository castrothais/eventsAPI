package br.edu.ifsudestemg.eventsAPI.api.controller;

import br.edu.ifsudestemg.eventsAPI.api.dto.IngressoDTO;
import br.edu.ifsudestemg.eventsAPI.api.dto.PagamentoIngressoDTO;
import br.edu.ifsudestemg.eventsAPI.api.exeption.RegraNegocioException;
import br.edu.ifsudestemg.eventsAPI.model.entity.*;
import br.edu.ifsudestemg.eventsAPI.service.CarteiraService;
import br.edu.ifsudestemg.eventsAPI.service.EventoService;
import br.edu.ifsudestemg.eventsAPI.service.IngressoService;
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
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/ingressos")
@RequiredArgsConstructor
@Tag(name = "Ingresso", description = "API de Ingressos")
public class IngressoController {

    private final IngressoService service;
    private final EventoService eventoService;
    private final UsuarioService usuarioService;
    private final CarteiraService carteiraService;

    @GetMapping()
    @Operation(summary = "Obter ingressos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ingressos encontrados"),
            @ApiResponse(responseCode = "404", description = "Nenhum ingresso encontrado")
    })
    public ResponseEntity get() {
        List<Ingresso> ingressos = service.getIngressos();
        return ResponseEntity.ok(ingressos.stream().map(IngressoDTO::create).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter informações de um ingresso")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ingresso encontrado"),
            @ApiResponse(responseCode = "404", description = "Ingresso não encontrado")
    })
    public ResponseEntity get( @PathVariable("id") @Parameter(description = "Id do ingresso") Long id) {
        Optional<Ingresso> ingresso = service.getIngressosById(id);
        if (!ingresso.isPresent()) {
            return new ResponseEntity("Ingresso não encontrado", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(ingresso.map(IngressoDTO::create));
    }

    @PostMapping()
    @Operation(summary = "Salvar ingresso")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ingresso salvo com sucesso"),
            @ApiResponse(responseCode = "400", description = "Falha ao salvar ingresso")
    })
    public ResponseEntity post(@Valid @RequestBody IngressoDTO dto) {
        try {
            Ingresso ingresso = converter(dto);
            ingresso = service.salvar(ingresso);
            return new ResponseEntity(IngressoDTO.create(ingresso), HttpStatus.CREATED);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PatchMapping("/{id}/pagamento")
    @Operation(summary = "Pagar ingresso")
    @Transactional
    public ResponseEntity pagarIngresso(

            @PathVariable("id") Long id,
            @RequestBody PagamentoIngressoDTO dto,
            Authentication authentication) {

        try {
            Optional<Ingresso> ingressoOpt = service.getIngressosById(id);

            if (ingressoOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Ingresso ingresso = ingressoOpt.get();

            // Identifica o participante autenticado
            Usuario usuario = usuarioService.buscarPorEmail(authentication.getName());

            if (!ingresso.getParticipante().getId().equals(usuario.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            // Verifica se o evento está cancelado
            if (ingresso.getEvento().isCancelado()) {
                throw new RegraNegocioException(
                        "Não é possível pagar um ingresso de evento cancelado."
                );
            }


            // Impede pagamento duplicado ou de ingresso cancelado
            if (ingresso.getStatus() != StatusIngresso.RESERVADO) {
                throw new RegraNegocioException(
                        "Este ingresso não está disponível para pagamento."
                );
            }

            // Calcula o valor conforme o tipo de ingresso
            BigDecimal valor = ingresso.getEvento().getValorIngresso();

            if (dto.getTipoIngresso() == null) {
                throw new RegraNegocioException("Informe o tipo de ingresso.");
            }

            switch (dto.getTipoIngresso().toLowerCase()) {
                case "inteira":
                    break;

                case "meia":
                    valor = valor.divide(
                            BigDecimal.valueOf(2),
                            2,
                            RoundingMode.HALF_UP
                    );
                    break;

                default:
                    throw new RegraNegocioException("Tipo de ingresso inválido.");
            }

            if (dto.getFormaPagamento() == null) {
                throw new RegraNegocioException("Informe a forma de pagamento.");
            }

            // Processa a forma de pagamento
            switch (dto.getFormaPagamento().toUpperCase()) {
                case "CARTEIRA":
                    if (valor.compareTo(BigDecimal.ZERO) > 0) {
                        carteiraService.debitar(usuario, valor, ingresso);
                    }
                    break;

                case "PIX":
                    // Pagamento simulado para fins acadêmicos
                    break;

                default:
                    throw new RegraNegocioException("Forma de pagamento inválida.");
            }

            ingresso.setTipoIngresso(dto.getTipoIngresso().toLowerCase());
            ingresso.setValor(valor);
            ingresso.setStatus(StatusIngresso.PAGO);

            service.salvar(ingresso);

            return ResponseEntity.ok(IngressoDTO.create(ingresso));

        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PutMapping("{id}")
    @Operation(summary = "Atualizar dados de ingresso")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ingresso atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Ingresso não encontrado")
    })
    public ResponseEntity atualizar( @PathVariable("id") @Parameter(description = "Id do ingresso") Long id, @Valid @RequestBody IngressoDTO dto) {
        if (!service.getIngressosById(id).isPresent()) {
            return new ResponseEntity("Ingresso não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            Ingresso ingresso = converter(dto);
            ingresso.setId(id);
            service.salvar(ingresso);
            return ResponseEntity.ok(ingresso);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public Ingresso converter(IngressoDTO dto) {
        Ingresso ingresso = new Ingresso();
        ingresso.setStatus(StatusIngresso.valueOf(dto.getStatus()));
        ingresso.setTaxa(dto.getTaxa());
        ingresso.setValor(dto.getValor());

        Usuario participante = usuarioService.getUsuariosById(dto.getIdParticipante())
                .orElseThrow(() -> new RegraNegocioException("Participante não encontrado"));
        ingresso.setParticipante(participante);

        Evento evento = eventoService
                .getEventosById(dto.getIdEvento())
                .orElseThrow(() -> new RegraNegocioException("Evento não encontrado"));

        ingresso.setEvento(evento);

        return ingresso;
    }
}

