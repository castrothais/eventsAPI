package br.edu.ifsudestemg.eventsAPI.api.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CredenciaisDTOTest {

    @Test
    void deveArmazenarEmailESenhaInformados() {
        CredenciaisDTO dto = new CredenciaisDTO();

        dto.setEmail("usuario@email.com");
        dto.setSenha("Senha123");

        assertEquals("usuario@email.com", dto.getEmail());
        assertEquals("Senha123", dto.getSenha());
    }

}