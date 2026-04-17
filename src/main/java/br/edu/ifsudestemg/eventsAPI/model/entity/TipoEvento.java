package br.edu.ifsudestemg.eventsAPI.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class TipoEvento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome do tipo de evento é obrigatório")
    @Size(min = 1, max = 255, message = "Nome deve ter entre 1 e 255 caracteres")
    private String nome;

    @Size(min = 1, max = 255, message = "A descrição deve ter entre 1 e 255 caracteres")
    private String descricao;
}
