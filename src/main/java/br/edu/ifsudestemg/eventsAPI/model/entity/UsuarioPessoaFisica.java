package br.edu.ifsudestemg.eventsAPI.model.entity;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class UsuarioPessoaFisica extends Usuario{
    private String nome;
    private String cpf;
    private LocalDate data;
    private String genero;

    @Override
    public String getNomeUsuario(){
        return this.nome;
    }
}
