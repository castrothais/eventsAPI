package br.edu.ifsudestemg.eventsAPI.api.dto;

import br.edu.ifsudestemg.eventsAPI.model.entity.Modalidade;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class ModalidadeDTO {
    private String modalidade;

    public static ModalidadeDTO create(Modalidade modalidade) {
        ModelMapper modelMapper = new ModelMapper();
        ModalidadeDTO dto = modelMapper.map(modalidade, ModalidadeDTO.class);
        dto.modalidade = modalidade.name();
        return dto;
    }
}
