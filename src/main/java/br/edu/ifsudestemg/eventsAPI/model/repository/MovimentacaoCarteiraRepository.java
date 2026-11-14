package br.edu.ifsudestemg.eventsAPI.model.repository;

import br.edu.ifsudestemg.eventsAPI.model.entity.MovimentacaoCarteira;
import br.edu.ifsudestemg.eventsAPI.model.entity.TipoMovimentacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimentacaoCarteiraRepository
        extends JpaRepository<MovimentacaoCarteira, Long> {

    List<MovimentacaoCarteira> findByCarteira_IdOrderByDataHoraDesc(Long carteiraId);

    boolean existsByIngresso_IdAndTipo(Long ingressoId, TipoMovimentacao tipo);
}