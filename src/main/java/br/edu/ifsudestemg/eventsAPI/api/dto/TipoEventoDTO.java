package br.edu.ifsudestemg.eventsAPI.api.dto;

import br.edu.ifsudestemg.eventsAPI.model.entity.TipoEvento;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class TipoEventoDTO {
    private Long id;
    private String nome;
    private String descricao;

    public static TipoEventoDTO create(TipoEvento tipoEvento) {
        ModelMapper modelMapper = new ModelMapper();
        TipoEventoDTO dto = modelMapper.map(tipoEvento, TipoEventoDTO.class);
        return dto;
    }
}
