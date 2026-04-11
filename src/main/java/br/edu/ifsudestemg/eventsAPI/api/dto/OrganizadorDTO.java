package br.edu.ifsudestemg.eventsAPI.api.dto;

import br.edu.ifsudestemg.eventsAPI.model.entity.Organizador;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class OrganizadorDTO {
    private Long id;
    private String nome;

    public static OrganizadorDTO create(Organizador organizador) {
        ModelMapper modelMapper = new ModelMapper();
        OrganizadorDTO dto = modelMapper.map(organizador, OrganizadorDTO.class);
        return dto;
    }
}
