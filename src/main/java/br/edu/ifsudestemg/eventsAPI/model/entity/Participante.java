package br.edu.ifsudestemg.eventsAPI.model.entity;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Participante extends Pessoa {
    private String genero;
}