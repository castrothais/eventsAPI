package br.edu.ifsudestemg.eventsAPI.model.entity;

import jakarta.persistence.*;
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

    private String nome;

    @ManyToOne
    private Evento evento;

    @ManyToOne
    private Ministrante ministrante;

    @ManyToOne
    private TipoAtividade tipoAtividade;
}
