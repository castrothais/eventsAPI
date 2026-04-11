package br.edu.ifsudestemg.eventsAPI.service;

import br.edu.ifsudestemg.eventsAPI.model.entity.Ingresso;
import br.edu.ifsudestemg.eventsAPI.model.repository.IngressoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
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
}