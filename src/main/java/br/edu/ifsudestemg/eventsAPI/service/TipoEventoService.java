package br.edu.ifsudestemg.eventsAPI.service;

import br.edu.ifsudestemg.eventsAPI.api.exeption.RegraNegocioException;
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

    public void validar(TipoEvento tipoEvento) {
        String nomeTrim = tipoEvento.getNome().trim();
        if (nomeTrim.isBlank()) {
            throw new RegraNegocioException("Nome não pode ser vazio");
        }
        if (!nomeTrim.matches(".*[a-zA-ZÀ-ÿ].*")) {
            throw new RegraNegocioException("Nome deve conter letras válidas");
        }

        if (tipoEvento.getId() == null && repository.existsByNomeIgnoreCase(tipoEvento.getNome())) {
            throw new RegraNegocioException("Já existe um tipo de evento com esse nome");
        }
    }
}