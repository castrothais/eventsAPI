package br.edu.ifsudestemg.eventsAPI.service;

import br.edu.ifsudestemg.eventsAPI.model.entity.PorteEvento;
import br.edu.ifsudestemg.eventsAPI.model.repository.PorteEventoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PorteEventoService {

    private PorteEventoRepository repository;

    public PorteEventoService(PorteEventoRepository repository) {
        this.repository = repository;
    }

    public List<PorteEvento> getPorteEventos() {
        return repository.findAll();
    }

    public Optional<PorteEvento> getPorteEventosById(Long id) {
        return repository.findById(id);
    }
}