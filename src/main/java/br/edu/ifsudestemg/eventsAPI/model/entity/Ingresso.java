package br.edu.ifsudestemg.eventsAPI.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Ingresso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Status do ingresso é obrigatório")
    @Enumerated(EnumType.STRING)
    private StatusIngresso status;

    @NotNull(message = "Valor do ingresso é obrigatório")
    @DecimalMin(value = "0.0")
    private BigDecimal valor;

    @DecimalMin(value = "0.0")
    private BigDecimal taxa;

    @NotNull(message = "O ingresso deve estar associada a um participante")
    @ManyToOne
    private Usuario participante;


    @NotNull(message = "O ingresso deve estar associada a um evento")
    @ManyToOne
    private Evento evento;
}
