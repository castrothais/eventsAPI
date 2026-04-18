package br.edu.ifsudestemg.eventsAPI.model.repository;

import br.edu.ifsudestemg.eventsAPI.model.entity.Atividade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AtividadeRepository extends JpaRepository<Atividade, Long> {
    public boolean existsByNomeIgnoreCaseAndEventoId(String nome, Long id);
    public Integer countByEventoId(Long id);
}