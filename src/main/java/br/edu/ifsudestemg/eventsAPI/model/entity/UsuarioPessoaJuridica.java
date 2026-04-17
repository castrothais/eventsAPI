package br.edu.ifsudestemg.eventsAPI.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class UsuarioPessoaJuridica extends Usuario{
    @NotBlank(message = "Razão social é obrigatório")
    @Size(min = 1, max = 255, message = "Razão social deve ter entre 1 e 255 caracteres")
    private String razaoSocial;

    @NotBlank(message = "Nome fantasia é obrigatório")
    @Size(min = 1, max = 255, message = "Nome fantasia deve ter entre 1 e 255 caracteres")
    private String nomeFantasia;

    @NotBlank(message = "CNPJ é obrigatório")
    @Pattern(regexp = "^\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}$", message = "Formato: 00.000.000/0000-00")
    private String cnpj;

    @NotBlank(message = "Iscrição Estadual ou municipal é obrigatório")
    @Size(min = 1, max = 50, message = "Iscrição Estadual ou municipal deve ter entre 1 e 255 caracteres")
    private String inscricaoEstadualMunicipal;

    @Override
    public String getNomeUsuario(){
        return this.nomeFantasia;
    }
}