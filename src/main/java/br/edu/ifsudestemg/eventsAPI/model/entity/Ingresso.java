package br.edu.ifsudestemg.eventsAPI.model.entity;

import jakarta.persistence.ManyToOne;

public class Ingresso {
    @ManyToOne
    private Participante participante;

}
