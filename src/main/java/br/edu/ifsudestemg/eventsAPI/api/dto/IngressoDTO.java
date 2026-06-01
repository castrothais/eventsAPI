package br.edu.ifsudestemg.eventsAPI.api.dto;

import br.edu.ifsudestemg.eventsAPI.model.entity.Ingresso;
import br.edu.ifsudestemg.eventsAPI.model.entity.StatusIngresso;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class IngressoDTO {
    private Long id;
    private String status;
    private BigDecimal valor;
    private BigDecimal taxa;

    private Long idParticipante;
    private String nomeParticipante;

    private Long idEvento;
    private String nomeEvento;

    public static IngressoDTO create(Ingresso ingresso) {
        ModelMapper modelMapper = new ModelMapper();
        IngressoDTO dto = modelMapper.map(ingresso, IngressoDTO.class);

        dto.nomeParticipante = ingresso.getParticipante().getNomeUsuario();
        dto.nomeEvento = ingresso.getEvento().getNome();

        return dto;
    }
}
