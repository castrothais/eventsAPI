package br.edu.ifsudestemg.eventsAPI.service;

import br.edu.ifsudestemg.eventsAPI.api.exeption.RegraNegocioException;
import br.edu.ifsudestemg.eventsAPI.model.entity.Atividade;
import br.edu.ifsudestemg.eventsAPI.model.entity.Perfil;
import br.edu.ifsudestemg.eventsAPI.model.repository.AtividadeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class AtividadeService {
    private AtividadeRepository repository;

    public AtividadeService(AtividadeRepository repository){
        this.repository = repository;
    }

    public List<Atividade> getAtividades() {
        return repository.findAll();
    }

    public Optional<Atividade> getAtividadesById(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public Atividade salvar(Atividade atividade){
        validar(atividade);
        return repository.save(atividade);
    }

    @Transactional
    public void excluir(Atividade atividade){
        Objects.requireNonNull(atividade.getId());
        repository.delete(atividade);
    }

    public void validar(Atividade atividade){
        String nomeTrim = atividade.getNome().trim();
        if (nomeTrim.isBlank()) {
            throw new RegraNegocioException("Nome não pode ser vazio");
        }
        if (!nomeTrim.matches(".*[a-zA-ZÀ-ÿ].*")) {
            throw new RegraNegocioException("Nome deve conter letras válidas");
        }
        if (atividade.getEvento() == null) {
            throw new RegraNegocioException("Evento é obrigatório");
        }

        if (atividade.getMinistrante() == null) {
            throw new RegraNegocioException("Ministrante é obrigatório");
        }

        if (atividade.getMinistrante().getPerfis() == null ||
                !atividade.getMinistrante().getPerfis().contains(Perfil.MEDIADOR)) {
            throw new RegraNegocioException("Usuário não possui perfil MEDIADOR");
        }

        if (atividade.getId() == null && repository.existsByNomeIgnoreCaseAndEventoId(atividade.getNome(), atividade.getEvento().getId())) {
            throw new RegraNegocioException("Já existe uma atividade com esse nome");
        }

        if (atividade.getEvento().getDataHoraFim() == null) {
            throw new RegraNegocioException("Evento sem data de término definida");
        }

        if (LocalDateTime.now().isAfter(atividade.getEvento().getDataHoraFim())){
            throw new RegraNegocioException("Criação/edição de atividade não permitida, evento principal finalizado");
        }

        if(atividade.isCancelado()) {
            validarCancelamentoAtividade(atividade);
        }


    }

    public void validarCancelamentoAtividade(Atividade atividade) {
        if(getAtividadesById(atividade.getId()).isEmpty() || atividade.getId() == null){
            throw new RegraNegocioException("Atividade não encontrada");
        }
        if (repository.countByEventoId(atividade.getEvento().getId()) <= 1 && !atividade.getEvento().isCancelado()) {
            throw new RegraNegocioException("Evento ativo deve possuir ao menos uma atividade");
        }
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
