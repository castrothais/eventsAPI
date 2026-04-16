package br.edu.ifsudestemg.eventsAPI.api.dto;

import br.edu.ifsudestemg.eventsAPI.model.entity.Ingresso;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class IngressoDTO {
    private Long id;

    private Long idParticipante;
    private String nomeParticipante;

    private Long idEvento;
    private String nomeEvento;

    public static IngressoDTO create(Ingresso ingresso) {
        ModelMapper modelMapper = new ModelMapper();
        IngressoDTO dto = modelMapper.map(ingresso, IngressoDTO.class);

        dto.nomeParticipante = ingresso.getParticipante().getNomeUsuario();
        dto.nomeEvento = ingresso.getEvento().getNome();

        return dto;
    }
}
