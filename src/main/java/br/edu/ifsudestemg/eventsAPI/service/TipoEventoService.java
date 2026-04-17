package br.edu.ifsudestemg.eventsAPI.service;

import br.edu.ifsudestemg.eventsAPI.model.entity.TipoEvento;
import br.edu.ifsudestemg.eventsAPI.model.repository.TipoEventoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TipoEventoService {

    private TipoEventoRepository repository;

    public TipoEventoService(TipoEventoRepository repository) {
        this.repository = repository;
    }

    public List<TipoEvento> getTipoEventos() {
        return repository.findAll();
    }

    public Optional<TipoEvento> getTipoEventosById(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public TipoEvento salvar(TipoEvento tipoEvento) {
        validar(tipoEvento);
        return repository.save(tipoEvento);
    }

    @Transactional
    public void excluir(TipoEvento tipoEvento) {
        Objects.requireNonNull(tipoEvento.getId());
        repository.delete(tipoEvento);
    }

    public void validar(TipoEvento tipoEvento) {};
}