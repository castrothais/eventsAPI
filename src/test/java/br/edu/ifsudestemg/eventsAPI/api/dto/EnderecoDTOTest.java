package br.edu.ifsudestemg.eventsAPI.api.dto;

import br.edu.ifsudestemg.eventsAPI.model.entity.Endereco;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnderecoDTOTest {

    @Test
    void deveCriarDTOComTodosOsAtributosPreenchidos() {
        Endereco endereco = new Endereco();

        endereco.setCep("36000-000");
        endereco.setEstado("Minas Gerais");
        endereco.setCidade("Juiz de Fora");
        endereco.setBairro("Centro");
        endereco.setLogradouro("Rua Halfeld");
        endereco.setNumero("100");
        endereco.setComplemento("Apartamento 101");

        EnderecoDTO dto = EnderecoDTO.create(endereco);

        assertNotNull(dto);
        assertEquals("36000-000", dto.getCep());
        assertEquals("Minas Gerais", dto.getEstado());
        assertEquals("Juiz de Fora", dto.getCidade());
        assertEquals("Centro", dto.getBairro());
        assertEquals("Rua Halfeld", dto.getLogradouro());
        assertEquals("100", dto.getNumero());
        assertEquals("Apartamento 101", dto.getComplemento());
    }

    @Test
    void deveCriarDTOComAtributosNulos() {
        Endereco endereco = new Endereco();

        EnderecoDTO dto = EnderecoDTO.create(endereco);

        assertNotNull(dto);
        assertNull(dto.getCep());
        assertNull(dto.getEstado());
        assertNull(dto.getCidade());
        assertNull(dto.getBairro());
        assertNull(dto.getLogradouro());
        assertNull(dto.getNumero());
        assertNull(dto.getComplemento());
    }
}