package br.edu.ifsudestemg.eventsAPI.api.dto;

import br.edu.ifsudestemg.eventsAPI.model.entity.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class PessoaDTO {
    private Long id;
    private String nome;
    private String cpf;
    private LocalDate data;
    private String senha;
    private String celular;
    private String email;

    public static PessoaDTO create(Usuario pessoa) {
        ModelMapper modelMapper = new ModelMapper();
        PessoaDTO dto = modelMapper.map(pessoa, PessoaDTO.class);
        return dto;
    }
}
