package br.edu.ifsudestemg.eventsAPI.api.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenDTOTest {

    @Test
    void deveArmazenarLoginETokenInformados() {
        TokenDTO dto = new TokenDTO();

        dto.setLogin("usuario@email.com");
        dto.setTokenDTO("abc123token");

        assertEquals("usuario@email.com", dto.getLogin());
        assertEquals("abc123token", dto.getTokenDTO());
    }

    @Test
    void deveManterAtributosNulosQuandoNaoInformados() {
        TokenDTO dto = new TokenDTO();

        assertNull(dto.getLogin());
        assertNull(dto.getTokenDTO());
    }

}