package br.edu.ifsudestemg.eventsAPI.api.dto;

import br.edu.ifsudestemg.eventsAPI.model.entity.Atividade;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class AtividadeDTO {

    private Long id;
    private String nome;
    private Long idEvento;
    private String nomeEvento;
    private Long idMinistrante;
    private String nomeMinistrante;
    private Long idTipoAtividade;
    private String nomeTipoAtividade;

    public static AtividadeDTO create(Atividade atividade) {
        ModelMapper modelMapper = new ModelMapper();
        AtividadeDTO dto = modelMapper.map(atividade, AtividadeDTO.class);
        dto.nomeEvento = atividade.getEvento().getNome();
        dto.nomeMinistrante = atividade.getMinistrante().getNomeUsuario();
        dto.nomeTipoAtividade = atividade.getTipoAtividade().getNome();
        return dto;
    }
}