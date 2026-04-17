package br.edu.ifsudestemg.eventsAPI.service;

import br.edu.ifsudestemg.eventsAPI.model.entity.PorteEvento;
import br.edu.ifsudestemg.eventsAPI.model.repository.PorteEventoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
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

    @Transactional
    public PorteEvento salvar(PorteEvento porteEvento) {
        validar(porteEvento);
        return repository.save(porteEvento);
    }

    @Transactional
    public void excluir(PorteEvento porteEvento) {
        Objects.requireNonNull(porteEvento.getId());
        repository.delete(porteEvento);
    }

    public void validar(PorteEvento porteEvento) {};
}