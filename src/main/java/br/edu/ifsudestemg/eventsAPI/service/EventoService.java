package br.edu.ifsudestemg.eventsAPI.service;

import br.edu.ifsudestemg.eventsAPI.model.entity.Evento;
import br.edu.ifsudestemg.eventsAPI.model.repository.EventoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class EventoService {
    private EventoRepository repository;

    public EventoService(EventoRepository repository) {
        this.repository = repository;
    }

    public List<Evento> getEventos() {
        return repository.findAll();
    }

    public Optional<Evento> getEventosById(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public Evento salvar(Evento evento) {
        validar(evento);
        return repository.save(evento);
    }

    @Transactional
    public void excluir(Evento evento) {
        Objects.requireNonNull(evento.getId());
        repository.delete(evento);
    }

    public void validar(Evento evento) {}
}