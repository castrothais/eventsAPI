package br.edu.ifsudestemg.eventsAPI.model.entity;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)

public class UsuarioPessoaFisica extends Usuario{
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 1, max = 255, message = "Nome deve ter entre 1 e 255 caracteres")
    private String nome;

    @NotBlank(message = "CPF é obrigatório")
    @Pattern(regexp = "^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$", message = "Formato: 000.000.000-00")
    private String cpf;

    @NotNull(message = "Data de nascimento é obrigatório")
    @Past
    private LocalDate data;

    @NotBlank(message = "Genêro é obrigatório")
    private String genero;

    @Override
    public String getNomeUsuario(){
        return this.nome;
    }
}
