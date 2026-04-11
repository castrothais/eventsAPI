package br.edu.ifsudestemg.eventsAPI.api.dto;

import br.edu.ifsudestemg.eventsAPI.model.entity.TipoAtividade;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class TipoAtividadeDTO {
    private Long id;
    private String nome;

    public static TipoAtividadeDTO create(TipoAtividade tipoAtividade) {
        ModelMapper modelMapper = new ModelMapper();
        TipoAtividadeDTO dto = modelMapper.map(tipoAtividade, TipoAtividadeDTO.class);
        return dto;
    }
}
