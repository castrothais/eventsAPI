package br.edu.ifsudestemg.eventsAPI.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Ministrante extends OrganizadorCPF {
    private String razaoSocial;
    private String nomeFantasia;
    private String cnpj;
    private String inscricaoEstadualMunicipal;

    @ManyToOne
    private Evento evento;
    @ManyToOne
    private Atividade atividade;
}
