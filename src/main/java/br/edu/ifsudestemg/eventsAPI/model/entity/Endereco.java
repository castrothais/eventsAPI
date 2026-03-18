package br.edu.ifsudestemg.eventsAPI.model.entity;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class Endereco {
    private String cep;
    private String estado;
    private String cidade;
    private String bairro;
    private String logradouro;
    private String numero;
    private String complemento;
}