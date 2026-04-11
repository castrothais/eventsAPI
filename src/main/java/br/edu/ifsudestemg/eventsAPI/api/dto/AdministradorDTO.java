package br.edu.ifsudestemg.eventsAPI.api.dto;

import br.edu.ifsudestemg.eventsAPI.model.entity.Administrador;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class AdministradorDTO {

    private String nome;
    private String cpf;
    private LocalDate data;
    private String celular;
    private String email;

    private String cidade;
    private String estado;

    public static AdministradorDTO create(Administrador admin) {
        ModelMapper modelMapper = new ModelMapper();

        AdministradorDTO dto = modelMapper.map(admin, AdministradorDTO.class);

        dto.cidade = admin.getEndereco().getCidade();
        dto.estado = admin.getEndereco().getEstado();

        return dto;
    }
}