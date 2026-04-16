package br.edu.ifsudestemg.eventsAPI.api.dto;

import br.edu.ifsudestemg.eventsAPI.model.entity.PorteEvento;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class PorteEventoDTO {
    private Long id;
    private String nome;
    private Integer minParticipantes;
    private Integer maxParticipantes;
    private Integer tempoMinimoCancelamento;
    private String descricao;

    public static PorteEventoDTO create(PorteEvento porteEvento) {
        ModelMapper modelMapper = new ModelMapper();
        PorteEventoDTO dto = modelMapper.map(porteEvento, PorteEventoDTO.class);
        return dto;
    }
}
