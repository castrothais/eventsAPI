package br.edu.ifsudestemg.eventsAPI.model.repository;

import br.edu.ifsudestemg.eventsAPI.model.entity.TipoAtividade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoAtividadeRepository extends JpaRepository<TipoAtividade, Long> {
    public boolean existsByNomeIgnoreCase(String nome);
}

