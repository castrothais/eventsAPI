package br.edu.ifsudestemg.eventsAPI.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class OrganizadorCNPJDTO {
    private Long id;
    private String nome;
    private String razaoSocial;
    private String nomeFantasia;
    private String cnpj;
    private String inscricaoEstadualMunicipal;

    public static OrganizadorCNPJDTO create(OrganizadorCNPJ organizadorCNPJ) {
        ModelMapper modelMapper = new ModelMapper();
        OrganizadorCNPJDTO dto = modelMapper.map(organizadorCNPJ, OrganizadorCNPJDTO.class);
        return dto;
    }
}
