package br.edu.ifsudestemg.eventsAPI.service;

import br.edu.ifsudestemg.eventsAPI.model.entity.TipoAtividade;
import br.edu.ifsudestemg.eventsAPI.model.repository.TipoAtividadeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TipoAtividadeService {

    private TipoAtividadeRepository repository;

    public TipoAtividadeService(TipoAtividadeRepository repository) {
        this.repository = repository;
    }

    public List<TipoAtividade> getTipoAtividades() {
        return repository.findAll();
    }

    public Optional<TipoAtividade> getTipoAtividadesById(Long id) {
        return repository.findById(id);
    }
}