package br.edu.ifsudestemg.eventsAPI.model.repository;

import br.edu.ifsudestemg.eventsAPI.model.entity.PorteEvento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PorteEventoRepository extends JpaRepository<PorteEvento, Long> {
    public boolean existsByNomeIgnoreCase(String nome);
}

