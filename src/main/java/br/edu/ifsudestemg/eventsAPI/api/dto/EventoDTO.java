package br.edu.ifsudestemg.eventsAPI.api.dto;

import br.edu.ifsudestemg.eventsAPI.model.entity.Evento;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class EventoDTO {
    private Long id;
    private String nome;
    private String modalidade;
    private LocalDateTime dataHoraInicio;
    private LocalDateTime dataHoraFim;
    private String descricao;
    private Integer idadeMinima;
    private Integer lotacaoMaxima;
    private boolean cancelado;

    private Long idOrganizador;
    private String nomeOrganizador;

    private Long idTipoEvento;
    private String nomeTipoEvento;

    private Long idPorteEvento;
    private String nomePorteEvento;

    public static EventoDTO create(Evento evento) {
        ModelMapper modelMapper = new ModelMapper();
        EventoDTO dto = modelMapper.map(evento, EventoDTO.class);

        dto.nomeOrganizador = evento.getOrganizador().getNomeUsuario();
        dto.nomeTipoEvento = evento.getTipoEvento().getNome();
        dto.nomePorteEvento = evento.getPorteEvento().getNome();

        return dto;
    }
}
