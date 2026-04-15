package br.edu.ifsudestemg.eventsAPI.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class OrganizadorCPFDTO {
    private Long id;
    private String nome;
    private String cpf;

    public static OrganizadorCPFDTO create(OrganizadorCPF organizadorCPF) {
        ModelMapper modelMapper = new ModelMapper();
        OrganizadorCPFDTO dto = modelMapper.map(organizadorCPF, OrganizadorCPFDTO.class);
        return dto;
    }
}
