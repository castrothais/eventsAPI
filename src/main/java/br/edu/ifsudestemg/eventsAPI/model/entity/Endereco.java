package br.edu.ifsudestemg.eventsAPI.model.entity;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.*;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Endereco {
    @NotBlank(message = "CEP obrigatório")
    @Pattern(regexp = "^\\d{5}-\\d{3}$", message = "CEP inválido (formato: 00000-000)")
    private String cep;

    @NotBlank(message = "Estado obrigatório")
    @Size(max = 2, message = "Use a sigla do estado (ex: MG)")
    private String estado;

    @NotBlank(message = "Cidade é obrigatória")
    @Size(min = 1, max = 255, message = "A cidade deve ter entre 1 e 255 caracteres")
    private String cidade;

    @NotBlank(message = "Bairro é obrigatório")
    @Size(min = 1, max = 255, message = "O bairro deve ter entre 1 e 255 caracteres")
    private String bairro;

    @NotBlank(message = "Logradouro é obrigatório")
    @Size(min = 1, max = 255, message = "O logradouro deve ter entre 1 e 255 caracteres")
    private String logradouro;

    @NotBlank(message = "Número é obrigatório")
    @Size(min = 1, max = 10, message = "O número deve ter entre 1 e 30 caracteres")
    private String numero;

    @Size(min = 1, max = 255, message = "O complemento deve ter entre 1 e 255 caracteres")
    private String complemento;
}