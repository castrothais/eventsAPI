package br.edu.ifsudestemg.eventsAPI.service;

import br.edu.ifsudestemg.eventsAPI.model.entity.Ingresso;
import br.edu.ifsudestemg.eventsAPI.model.repository.IngressoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class IngressoService {

    private IngressoRepository repository;

    public IngressoService(IngressoRepository repository) {
        this.repository = repository;
    }

    public List<Ingresso> getIngressos() {
        return repository.findAll();
    }

    public Optional<Ingresso> getIngressosById(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public Ingresso salvar(Ingresso ingresso) {
        return repository.save(ingresso);
    }

    @Transactional
    public void excluir(Ingresso ingresso) {
        Objects.requireNonNull(ingresso.getId());
        repository.delete(ingresso);
    }
}