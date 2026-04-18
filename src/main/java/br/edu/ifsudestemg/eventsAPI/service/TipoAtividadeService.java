package br.edu.ifsudestemg.eventsAPI.service;

import br.edu.ifsudestemg.eventsAPI.api.exeption.RegraNegocioException;
import br.edu.ifsudestemg.eventsAPI.model.entity.TipoAtividade;
import br.edu.ifsudestemg.eventsAPI.model.repository.TipoAtividadeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TipoAtividadeService {

    private TipoAtividadeRepository repository;

    public TipoAtividadeService(TipoAtividadeRepository repository) {
        this.repository = repository;
    }

    public List<TipoAtividade> getTipoAtividades() {
        return repository.findAll();
    }

    public Optional<TipoAtividade> getTipoAtividadesById(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public TipoAtividade salvar(TipoAtividade tipoAtividade) {
        validar(tipoAtividade);
        return repository.save(tipoAtividade);
    }

    @Transactional
    public void excluir(TipoAtividade tipoAtividade) {
        Objects.requireNonNull(tipoAtividade.getId());
        repository.delete(tipoAtividade);
    }

    public void validar(TipoAtividade tipoAtividade) {
        String nomeTrim = tipoAtividade.getNome().trim();
        if (nomeTrim.isBlank()) {
            throw new RegraNegocioException("Nome não pode ser vazio");
        }
        if (!nomeTrim.matches(".*[a-zA-ZÀ-ÿ].*")) {
            throw new RegraNegocioException("Nome deve conter letras válidas");
        }

        if (tipoAtividade.getId() == null && repository.existsByNomeIgnoreCase(tipoAtividade.getNome())) {
            throw new RegraNegocioException("Já existe um tipo de atividade com esse nome");
        }
    }
}