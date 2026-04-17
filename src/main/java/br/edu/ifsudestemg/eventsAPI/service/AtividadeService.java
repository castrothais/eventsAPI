package br.edu.ifsudestemg.eventsAPI.service;

import br.edu.ifsudestemg.eventsAPI.model.entity.Atividade;
import br.edu.ifsudestemg.eventsAPI.model.repository.AtividadeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class AtividadeService {
    private AtividadeRepository repository;

    public AtividadeService(AtividadeRepository repository){
        this.repository = repository;
    }

    public List<Atividade> getAtividades() {
        return repository.findAll();
    }

    public Optional<Atividade> getAtividadesById(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public Atividade salvar(Atividade atividade){
        validar(atividade);
        return repository.save(atividade);
    }

    @Transactional
    public void excluir(Atividade atividade){
        Objects.requireNonNull(atividade.getId());
        repository.delete(atividade);
    }

    public void validar(Atividade atividade){}
}
