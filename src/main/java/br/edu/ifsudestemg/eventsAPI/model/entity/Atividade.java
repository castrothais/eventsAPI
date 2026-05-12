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

public class Atividade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome da atividade é obrigatório")
    @Size(min = 1, max = 255, message = "Nome deve ter entre 1 e 255 caracteres")
    private String nome;

    @NotNull
    private boolean cancelado = false;

    @NotNull(message = "A atividade deve estar associada a um evento")
    @ManyToOne
    private Evento evento;

    @NotNull(message = "A atividade deve estar associada a um ministrante")
    @ManyToOne
    private Usuario ministrante;

    @NotNull(message = "A atividade deve ter um tipo de atividade")
    @ManyToOne
    private TipoAtividade tipoAtividade;
}
