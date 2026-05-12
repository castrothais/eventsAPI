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

public class Presenca {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private boolean presente;

    @NotNull(message = "A presença deve estar associada a um participante")
    @ManyToOne
    private Usuario participante;

    @NotNull(message = "A presença deve estar associada a uma atividade")
    @ManyToOne
    private Atividade atividade;
}
