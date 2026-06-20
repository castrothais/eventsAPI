package br.edu.ifsudestemg.eventsAPI.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Evento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome do evento obrigatório")
    @Size(min = 1, max = 255, message = "Nome do evento deve ter entre 1 e 255 caracteres")
    private String nome;

    private String imagem;

    @NotNull(message = "Modalidade obrigatória")
    @Enumerated(EnumType.STRING)
    private Modalidade modalidade;

    @NotNull(message = "Data e hora de início do evento são obrigatórias")
    @FutureOrPresent
    private LocalDateTime dataHoraInicio;

    @NotNull(message = "Data e hora de fim do evento são obrigatórias")
    @FutureOrPresent
    private LocalDateTime dataHoraFim;

    @Size(min = 1, max = 500, message = "A descrição deve ter entre 1 e 255 caracteres")
    private String descricao;

    @Min(0)
    @Max(18)
    @NotNull(message = "Idade mínima obrigatória")
    private Integer idadeMinima;

    @NotNull(message = "Lotação máxima obrigatória")
    @Min(1)
    @Max(10000)
    private Integer lotacaoMaxima;

    @NotNull(message = "O evento deve estar associada a um endereço")
    @Embedded
    private Endereco endereco;

    @NotNull(message = "Valor do ingresso obrigatório")
    @DecimalMin(value = "0.0")
    private BigDecimal valorIngresso;

    @NotNull
    private boolean cancelado = false;

    @NotNull(message = "A atividade deve estar associada a um organizador")
    @ManyToOne
    private Usuario organizador;

    @NotNull(message = "A atividade deve ter um tipo de evento")
    @ManyToOne
    private TipoEvento tipoEvento;

    @NotNull(message = "A atividade deve ter um porte de evento")
    @ManyToOne
    private PorteEvento porteEvento;
}
