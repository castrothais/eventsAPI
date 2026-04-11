package br.edu.ifsudestemg.eventsAPI.model.repository;

import br.edu.ifsudestemg.eventsAPI.model.entity.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PessoaRepository extends JpaRepository<Pessoa, Long> {
}

