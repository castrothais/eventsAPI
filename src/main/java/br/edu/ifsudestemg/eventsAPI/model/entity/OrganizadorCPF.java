package br.edu.ifsudestemg.eventsAPI.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor


public class OrganizadorCPF extends Pessoa {
    private String genero;

    @ManyToOne
    private Evento evento;

}