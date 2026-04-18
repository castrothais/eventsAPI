package br.edu.ifsudestemg.eventsAPI.service;

import br.edu.ifsudestemg.eventsAPI.api.exeption.RegraNegocioException;
import br.edu.ifsudestemg.eventsAPI.model.entity.Evento;
import br.edu.ifsudestemg.eventsAPI.model.entity.Presenca;
import br.edu.ifsudestemg.eventsAPI.model.repository.PresencaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
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

    @Transactional
    public Presenca salvar(Presenca presenca) {
        validar(presenca);
        return repository.save(presenca);
    }

    @Transactional
    public void excluir(Presenca presenca) {
        Objects.requireNonNull(presenca.getId());
        repository.delete(presenca);
    }

    public void validar(Presenca presenca) {
        Evento evento = presenca.getAtividade().getEvento();
        if(LocalDateTime.now().isBefore(evento.getDataHoraInicio()) || LocalDateTime.now().isAfter(evento.getDataHoraFim())) {
            throw new RegraNegocioException("A presença não pode ser alterada fora da realização do evento");
        }
    }
}