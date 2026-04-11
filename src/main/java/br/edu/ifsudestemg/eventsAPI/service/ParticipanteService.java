package br.edu.ifsudestemg.eventsAPI.service;

import br.edu.ifsudestemg.eventsAPI.model.entity.Participante;
import br.edu.ifsudestemg.eventsAPI.model.repository.ParticipanteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ParticipanteService {

    private ParticipanteRepository repository;

    public ParticipanteService(ParticipanteRepository repository) {
        this.repository = repository;
    }

    public List<Participante> getParticipantes() {
        return repository.findAll();
    }

    public Optional<Participante> getParticipantesById(Long id) {
        return repository.findById(id);
    }
}