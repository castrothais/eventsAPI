package br.edu.ifsudestemg.eventsAPI.api.dto;

import br.edu.ifsudestemg.eventsAPI.model.entity.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioDTOTest {

    @Test
    void deveCriarDTOParaUsuarioPessoaFisica() {
        UsuarioPessoaFisica usuario = new UsuarioPessoaFisica();

        Endereco endereco = new Endereco();
        endereco.setCidade("Juiz de Fora");

        usuario.setId(1L);
        usuario.setCelular("(32) 99999-9999");
        usuario.setEmail("maria@email.com");
        usuario.setEndereco(endereco);
        usuario.setPerfis(List.of(Perfil.PARTICIPANTE, Perfil.ORGANIZADOR));

        usuario.setNome("Maria Silva");
        usuario.setCpf("123.456.789-00");
        usuario.setData(LocalDate.of(2000, 5, 10));
        usuario.setGenero("Feminino");

        UsuarioDTO dto = UsuarioDTO.create(usuario);

        assertNotNull(dto);

        assertEquals(1L, dto.getId());
        assertEquals("(32) 99999-9999", dto.getCelular());
        assertEquals("maria@email.com", dto.getEmail());
        assertEquals("Juiz de Fora", dto.getEndereco().getCidade());

        assertEquals(TipoUsuario.PF, dto.getTipoUsuario());

        assertEquals("Maria Silva", dto.getNome());
        assertEquals("123.456.789-00", dto.getCpf());
        assertEquals(LocalDate.of(2000, 5, 10), dto.getData());
        assertEquals("Feminino", dto.getGenero());

        assertEquals(List.of("PARTICIPANTE", "ORGANIZADOR"), dto.getPerfis());

        assertNull(dto.getCnpj());
        assertNull(dto.getRazaoSocial());
        assertNull(dto.getNomeFantasia());
        assertNull(dto.getInscricaoEstadualMunicipal());
    }

    @Test
    void deveCriarDTOParaUsuarioPessoaJuridica() {
        UsuarioPessoaJuridica usuario = new UsuarioPessoaJuridica();

        Endereco endereco = new Endereco();
        endereco.setCidade("Juiz de Fora");

        usuario.setId(2L);
        usuario.setCelular("(32) 98888-8888");
        usuario.setEmail("contato@empresa.com");
        usuario.setEndereco(endereco);
        usuario.setPerfis(List.of(Perfil.ADMINISTRADOR, Perfil.ORGANIZADOR));

        usuario.setRazaoSocial("Empresa Exemplo LTDA");
        usuario.setNomeFantasia("Empresa Exemplo");
        usuario.setCnpj("12.345.678/0001-90");
        usuario.setInscricaoEstadualMunicipal("123456789");

        UsuarioDTO dto = UsuarioDTO.create(usuario);

        assertNotNull(dto);

        assertEquals(2L, dto.getId());
        assertEquals("(32) 98888-8888", dto.getCelular());
        assertEquals("contato@empresa.com", dto.getEmail());
        assertEquals("Juiz de Fora", dto.getEndereco().getCidade());

        assertEquals(TipoUsuario.PJ, dto.getTipoUsuario());
        assertEquals("Empresa Exemplo LTDA", dto.getRazaoSocial());
        assertEquals("Empresa Exemplo", dto.getNomeFantasia());
        assertEquals("12.345.678/0001-90", dto.getCnpj());
        assertEquals("123456789", dto.getInscricaoEstadualMunicipal());

        assertEquals(
                List.of("ADMINISTRADOR", "ORGANIZADOR"),
                dto.getPerfis()
        );

        assertNull(dto.getNome());
        assertNull(dto.getCpf());
        assertNull(dto.getData());
        assertNull(dto.getGenero());
    }

    @Test
    void deveCriarDTOParaUsuarioPessoaFisicaComPerfisNulos() {
        UsuarioPessoaFisica usuario = new UsuarioPessoaFisica();

        usuario.setNome("Maria Silva");
        usuario.setPerfis(null);

        UsuarioDTO dto = UsuarioDTO.create(usuario);

        assertNotNull(dto);
        assertEquals(TipoUsuario.PF, dto.getTipoUsuario());
        assertEquals("Maria Silva", dto.getNome());
        assertNull(dto.getPerfis());
    }

    @Test
    void deveCriarDTOParaUsuarioPessoaJuridicaComPerfisNulos() {
        UsuarioPessoaJuridica usuario = new UsuarioPessoaJuridica();

        usuario.setNomeFantasia("Empresa Exemplo");
        usuario.setPerfis(null);

        UsuarioDTO dto = UsuarioDTO.create(usuario);

        assertNotNull(dto);
        assertEquals(TipoUsuario.PJ, dto.getTipoUsuario());
        assertEquals("Empresa Exemplo", dto.getNomeFantasia());
        assertNull(dto.getPerfis());
    }
}
