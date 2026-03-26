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

public class OrganizadorCNPJ extends Pessoa{
    private String razaoSocial;
    private String nomeFantasia;
    private String cnpj;
    private String inscricaoEstadualMunicipal;

    @ManyToOne
    private Evento evento;

}
