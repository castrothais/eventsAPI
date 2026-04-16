package br.edu.ifsudestemg.eventsAPI.service;

import br.edu.ifsudestemg.eventsAPI.model.entity.Presenca;
import br.edu.ifsudestemg.eventsAPI.model.repository.PresencaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PresencaService {

    private PresencaRepository repository;

    public PresencaService(PresencaRepository repository) {
        this.repository = repository;
    }

    public List<Presenca> getPresencas() {
        return repository.findAll();
    }

    public Optional<Presenca> getPresencasById(Long id) {
        return repository.findById(id);
    }
}