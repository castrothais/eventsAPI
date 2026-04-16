package br.edu.ifsudestemg.eventsAPI.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class ParticipanteDTO {
    private Long id;
    private String nome;
    private String cpf;
    private String email;
    private String genero;

    public static ParticipanteDTO create(Participante participante) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(participante, ParticipanteDTO.class);
    }
}
