package br.edu.ifsudestemg.eventsAPI.api.dto;

import br.edu.ifsudestemg.eventsAPI.model.entity.Presenca;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class PresencaDTO {
    private Long id;
    private Boolean presente;

    private Long idParticipante;
    private String nomeParticipante;

    private Long idAtividade;
    private String nomeAtividade;

    public static PresencaDTO create(Presenca presenca) {
        ModelMapper modelMapper = new ModelMapper();
        PresencaDTO dto = modelMapper.map(presenca, PresencaDTO.class);

        dto.nomeParticipante = presenca.getParticipante().getNome();
        dto.nomeAtividade = presenca.getAtividade().getNome();

        return dto;
    }
}
