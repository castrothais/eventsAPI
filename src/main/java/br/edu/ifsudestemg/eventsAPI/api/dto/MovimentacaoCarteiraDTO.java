package br.edu.ifsudestemg.eventsAPI.api.dto;

import br.edu.ifsudestemg.eventsAPI.model.entity.MovimentacaoCarteira;
import br.edu.ifsudestemg.eventsAPI.model.entity.TipoMovimentacao;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MovimentacaoCarteiraDTO {

    private Long id;
    private TipoMovimentacao tipo;
    private BigDecimal valor;
    private LocalDateTime dataHora;
    private Long idIngresso;

    public static MovimentacaoCarteiraDTO create(
            MovimentacaoCarteira movimentacao) {

        MovimentacaoCarteiraDTO dto = new MovimentacaoCarteiraDTO();

        dto.setId(movimentacao.getId());
        dto.setTipo(movimentacao.getTipo());
        dto.setValor(movimentacao.getValor());
        dto.setDataHora(movimentacao.getDataHora());

        if (movimentacao.getIngresso() != null) {
            dto.setIdIngresso(movimentacao.getIngresso().getId());
        }

        return dto;
    }
}