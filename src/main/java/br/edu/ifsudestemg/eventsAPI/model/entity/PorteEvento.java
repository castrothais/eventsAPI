package br.edu.ifsudestemg.eventsAPI.model.entity;

import jakarta.persistence.*;
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

    private String nome;
    private Integer minParticipantes;
    private Integer maxParticipantes;
    private Integer tempoMinimoCancelamento;
    private String descricao;

    @ManyToOne
    private Evento evento;
}
