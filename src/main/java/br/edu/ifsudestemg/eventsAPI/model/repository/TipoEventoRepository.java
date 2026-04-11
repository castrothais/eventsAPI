package br.edu.ifsudestemg.eventsAPI.model.repository;

import br.edu.ifsudestemg.eventsAPI.model.entity.TipoEvento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoEventoRepository extends JpaRepository<TipoEvento, Long> {
}

