package br.edu.ifsudestemg.eventsAPI.api.controller;

import br.edu.ifsudestemg.eventsAPI.api.dto.MovimentacaoCarteiraDTO;
import br.edu.ifsudestemg.eventsAPI.model.entity.Carteira;
import br.edu.ifsudestemg.eventsAPI.model.entity.MovimentacaoCarteira;
import br.edu.ifsudestemg.eventsAPI.service.CarteiraService;
import br.edu.ifsudestemg.eventsAPI.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/carteira")
@RequiredArgsConstructor
public class CarteiraController {

    private final CarteiraService carteiraService;
    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<BigDecimal> consultarSaldo(
            Authentication authentication) {

        Long usuarioId = usuarioService
                .buscarPorEmail(authentication.getName())
                .getId();

        BigDecimal saldo = carteiraService.consultarSaldo(usuarioId);

        return ResponseEntity.ok(saldo);
    }

    @GetMapping("/movimentacoes")
    public ResponseEntity<List<MovimentacaoCarteiraDTO>> consultarMovimentacoes(
            Authentication authentication) {

        var usuario = usuarioService.buscarPorEmail(authentication.getName());

        Carteira carteira = carteiraService.obterOuCriarCarteira(usuario);

        List<MovimentacaoCarteira> movimentacoes =
                carteiraService.consultarMovimentacoes(carteira.getId());

        List<MovimentacaoCarteiraDTO> dtos = movimentacoes.stream()
                .map(MovimentacaoCarteiraDTO::create)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }
}