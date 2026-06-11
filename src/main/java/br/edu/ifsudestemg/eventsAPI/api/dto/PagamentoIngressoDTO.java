package br.edu.ifsudestemg.eventsAPI.api.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PagamentoIngressoDTO {

    private BigDecimal valor;
    private String tipoIngresso;
}
