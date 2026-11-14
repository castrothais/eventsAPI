package br.edu.ifsudestemg.eventsAPI.model.repository;

import br.edu.ifsudestemg.eventsAPI.model.entity.Ingresso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IngressoRepository extends JpaRepository<Ingresso, Long> {
    List<Ingresso> findByEvento_Id(Long eventoId);
}

