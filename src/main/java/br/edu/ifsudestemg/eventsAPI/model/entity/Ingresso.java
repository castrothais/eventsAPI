package br.edu.ifsudestemg.eventsAPI.model.entity;

import jakarta.persistence.*;

@Entity

public class Ingresso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Participante participante;

    @ManyToOne
    private Evento evento;
}
