package br.edu.ifsudestemg.eventsAPI.api.dto;

import br.edu.ifsudestemg.eventsAPI.model.entity.Ministrante;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class MinistranteDTO {
    private Long id;
    private String nome;

    public static MinistranteDTO create(Ministrante ministrante) {
        ModelMapper modelMapper = new ModelMapper();
        MinistranteDTO dto = modelMapper.map(ministrante, MinistranteDTO.class);
        return dto;
    }
}
