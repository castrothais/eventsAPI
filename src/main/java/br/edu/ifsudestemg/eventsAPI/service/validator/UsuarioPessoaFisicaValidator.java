package br.edu.ifsudestemg.eventsAPI.service.validator;

import br.edu.ifsudestemg.eventsAPI.api.exeption.RegraNegocioException;
import br.edu.ifsudestemg.eventsAPI.model.entity.Usuario;
import br.edu.ifsudestemg.eventsAPI.model.entity.UsuarioPessoaFisica;
import br.edu.ifsudestemg.eventsAPI.model.repository.UsuarioPessoaFisicaRepository;
import org.springframework.stereotype.Component;

@Component
public class UsuarioPessoaFisicaValidator implements UsuarioValidator{
    private final UsuarioPessoaFisicaRepository repository;

    public UsuarioPessoaFisicaValidator(UsuarioPessoaFisicaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void validar(Usuario usuario) {
        if (usuario instanceof UsuarioPessoaFisica usuarioPessoaFisica) {
            String nomeTrim = usuarioPessoaFisica.getNome().trim();
            if (nomeTrim.isBlank()) {
                throw new RegraNegocioException("Nome não pode ser vazio");
            }
            if (!nomeTrim.matches(".*[a-zA-ZÀ-ÿ].*")) {
                throw new RegraNegocioException("Nome deve conter letras válidas");
            }

            if (usuarioPessoaFisica.getId() == null && repository.existsByCpf(usuarioPessoaFisica.getCpf())) {
                throw new RegraNegocioException("CPF já cadastrado");
            }
        } else {
            return;
        }
    }
}
