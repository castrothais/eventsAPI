package br.edu.ifsudestemg.eventsAPI.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Ingresso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "O ingresso deve estar associada a um participante")
    @ManyToOne
    private Usuario participante;

    @NotNull(message = "O ingresso deve estar associada a um evento")
    @ManyToOne
    private Evento evento;
}
