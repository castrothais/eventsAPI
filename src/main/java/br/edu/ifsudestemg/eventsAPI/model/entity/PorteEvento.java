package br.edu.ifsudestemg.eventsAPI.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PorteEvento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome do porte de evento é obrigatório")
    @Size(min = 1, max = 255, message = "Nome deve ter entre 1 e 255 caracteres")
    private String nome;

    @Min(0)
    @Max(10000)
    @NotNull(message = "Lotação mínima é obrigatória")
    private Integer minParticipantes;

    @Min(1)
    @Max(10000)
    @NotBlank(message = "Lotação máxima é obrigatória")
    private Integer maxParticipantes;

    @Min(1)
    @Max(17568)
    @NotBlank(message = "Tempo mínimo de cancelamento é obrigatório (mais que 0 horas)")
    private Integer tempoMinimoCancelamento;

    @Size(min = 1, max = 255, message = "A descrição deve ter entre 1 e 255 caracteres")
    private String descricao;
}
