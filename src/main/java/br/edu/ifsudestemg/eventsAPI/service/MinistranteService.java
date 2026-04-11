package br.edu.ifsudestemg.eventsAPI.service;

import br.edu.ifsudestemg.eventsAPI.model.entity.Ministrante;
import br.edu.ifsudestemg.eventsAPI.model.repository.MinistranteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MinistranteService {

    private MinistranteRepository repository;

    public MinistranteService(MinistranteRepository repository) {
        this.repository = repository;
    }

    public List<Ministrante> getMinistrantes() {
        return repository.findAll();
    }

    public Optional<Ministrante> getMinistrantesById(Long id) {
        return repository.findById(id);
    }
}