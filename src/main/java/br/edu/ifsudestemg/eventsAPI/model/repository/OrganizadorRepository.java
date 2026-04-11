package br.edu.ifsudestemg.eventsAPI.model.repository;

import br.edu.ifsudestemg.eventsAPI.model.entity.Organizador;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizadorRepository extends JpaRepository<Organizador, Long> {
}

