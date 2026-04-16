package br.edu.ifsudestemg.eventsAPI.api.dto;

import br.edu.ifsudestemg.eventsAPI.model.entity.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class UsuarioDTO {
    private Long id;
    private String nome;
    private String celular;
    private String email;
    private List<String> perfis;

    private String cpf;
    private LocalDate data;
    private String genero;

    private String cnpj;
    private String razaoSocial;
    private String nomeFantasia;
    private String inscricaoEstadualMunicipal;

    public static UsuarioDTO create(Usuario usuario) {
        ModelMapper modelMapper = new ModelMapper();
        UsuarioDTO dto = modelMapper.map(usuario, UsuarioDTO.class);

        dto.nome = usuario.getNomeUsuario();

        if (usuario.getPerfis() != null) {
            dto.setPerfis(
                    usuario.getPerfis()
                            .stream()
                            .map(Enum::name)
                            .toList()
            );
        }

        if (usuario instanceof UsuarioPessoaFisica usuarioPF) {
            dto.cpf = usuarioPF.getCpf();
            dto.data = usuarioPF.getData();
            dto.genero = usuarioPF.getGenero();
        }

        if (usuario instanceof UsuarioPessoaJuridica usuarioPJ) {
            dto.cnpj = usuarioPJ.getCnpj();
            dto.razaoSocial = usuarioPJ.getRazaoSocial();
            dto.inscricaoEstadualMunicipal = usuarioPJ.getInscricaoEstadualMunicipal();
        }

        return dto;
    }
}