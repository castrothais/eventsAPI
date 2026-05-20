package br.edu.ifsudestemg.eventsAPI.api.dto;

import br.edu.ifsudestemg.eventsAPI.model.entity.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UsuarioDTO {

    private Long id;

    private TipoUsuario tipoUsuario;
    private String celular;
    private String email;
    private Endereco endereco;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String senha;

    private List<String> perfis;

    // CPF
    private String nome;
    private String cpf;
    private LocalDate data;
    private String genero;

    // CNPJ
    private String cnpj;
    private String razaoSocial;
    private String nomeFantasia;
    private String inscricaoEstadualMunicipal;

    public static UsuarioDTO create(Usuario usuario) {

        UsuarioDTO dto = new UsuarioDTO();

        dto.setId(usuario.getId());
        dto.setCelular(usuario.getCelular());
        dto.setEmail(usuario.getEmail());
        dto.setEndereco(usuario.getEndereco());

        if (usuario.getPerfis() != null) {
            dto.setPerfis(
                    usuario.getPerfis()
                            .stream()
                            .map(Enum::name)
                            .toList()
            );
        }

        if (usuario instanceof UsuarioPessoaFisica usuarioPF) {

            dto.setTipoUsuario(TipoUsuario.PF);

            dto.setNome(usuarioPF.getNome());
            dto.setCpf(usuarioPF.getCpf());
            dto.setData(usuarioPF.getData());
            dto.setGenero(usuarioPF.getGenero());
        }

        if (usuario instanceof UsuarioPessoaJuridica usuarioPJ) {

            dto.setTipoUsuario(TipoUsuario.PJ);

            dto.setRazaoSocial(usuarioPJ.getRazaoSocial());
            dto.setNomeFantasia(usuarioPJ.getNomeFantasia());
            dto.setCnpj(usuarioPJ.getCnpj());
            dto.setInscricaoEstadualMunicipal(
                    usuarioPJ.getInscricaoEstadualMunicipal()
            );
        }

        return dto;
    }
}