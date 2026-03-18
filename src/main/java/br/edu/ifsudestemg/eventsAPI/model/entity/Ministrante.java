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
}
