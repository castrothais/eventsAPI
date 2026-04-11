package br.edu.ifsudestemg.eventsAPI.model.repository;

import br.edu.ifsudestemg.eventsAPI.model.entity.Ministrante;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MinistranteRepository extends JpaRepository<Ministrante, Long> {
}

