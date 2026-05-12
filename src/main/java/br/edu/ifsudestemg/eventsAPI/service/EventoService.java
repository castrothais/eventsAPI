package br.edu.ifsudestemg.eventsAPI.service;

import br.edu.ifsudestemg.eventsAPI.api.exeption.RegraNegocioException;
import br.edu.ifsudestemg.eventsAPI.model.entity.Evento;
import br.edu.ifsudestemg.eventsAPI.model.repository.EventoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

    public void validar(Evento evento) {
        String nomeTrim = evento.getNome().trim();
        if (nomeTrim.isBlank()) {
            throw new RegraNegocioException("Nome não pode ser vazio");
        }
        if (!nomeTrim.matches(".*[a-zA-ZÀ-ÿ].*")) {
            throw new RegraNegocioException("Nome deve conter letras válidas");
        }

        if (evento.getId() == null && repository.existsByNomeIgnoreCase(evento.getNome())) {
            throw new RegraNegocioException("Já existe uma evento com esse nome");
        }

        if(evento.getDataHoraInicio().isBefore(evento.getDataHoraFim()) ) {
            throw new RegraNegocioException("Data fim deve ser após a data início");
        }
        if(LocalDateTime.now().getYear() + 2 < evento.getDataHoraInicio().getYear()) {
            throw new RegraNegocioException("Só é possível marcar um evento com até 2 anos de antecedência");
        }
        if(evento.getDataHoraInicio().isAfter(LocalDateTime.now().plusYears(2))) {
            throw new RegraNegocioException("Só é possível marcar um evento com até 2 anos de antecedência");
        }
        if(evento.getDataHoraInicio().isBefore(LocalDateTime.now()) || evento.getDataHoraFim().isBefore(LocalDateTime.now())){
            throw new RegraNegocioException("Não é possível criar um evento antes da data atual");
        }

        if (LocalDateTime.now().isAfter(evento.getDataHoraFim())){
            throw new RegraNegocioException("Edição não permitida, evento finalizado");
        }

        if(evento.getLotacaoMaxima() > evento.getPorteEvento().getMaxParticipantes()){
            throw new RegraNegocioException("Lotação acima do máximo do porte");
        }
        if(evento.getLotacaoMaxima() < evento.getPorteEvento().getMinParticipantes()){
            throw new RegraNegocioException("Lotação abaixo do mínimo do porte");
        }

        if(evento.isCancelado()) {
            cancelarEvento(evento);
        }
    }

    public void cancelarEvento(Evento evento) {
        if(getEventosById(evento.getId()).isEmpty() || evento.getId() == null){
            throw new RegraNegocioException("Evento não encontrado");
        }

        long horasRestantes = Math.max(0, ChronoUnit.HOURS.between(LocalDateTime.now(), evento.getDataHoraInicio()));
        if (horasRestantes < evento.getPorteEvento().getTempoMinimoCancelamento()) {
            throw new RegraNegocioException("Cancelamento não permitido, tempo de cncelamento inferior ao permitido " +
                    evento.getPorteEvento().getTempoMinimoCancelamento() + " horas");
        }
    }
}