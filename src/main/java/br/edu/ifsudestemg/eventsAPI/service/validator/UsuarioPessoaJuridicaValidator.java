package br.edu.ifsudestemg.eventsAPI.service.validator;

import br.edu.ifsudestemg.eventsAPI.api.exeption.RegraNegocioException;
import br.edu.ifsudestemg.eventsAPI.model.entity.Usuario;
import br.edu.ifsudestemg.eventsAPI.model.entity.UsuarioPessoaJuridica;
import br.edu.ifsudestemg.eventsAPI.model.repository.UsuarioPessoaJuridicaRepository;
import org.springframework.stereotype.Component;

@Component
public class UsuarioPessoaJuridicaValidator implements UsuarioValidator{
    private final UsuarioPessoaJuridicaRepository repository;

    public UsuarioPessoaJuridicaValidator(UsuarioPessoaJuridicaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void validar(Usuario usuario) {
        if (usuario instanceof UsuarioPessoaJuridica usuarioPessoaJuridica) {
            String razaoSocialTrim = usuarioPessoaJuridica.getRazaoSocial().trim();
            if (razaoSocialTrim.isBlank()) {
                throw new RegraNegocioException("Nome não pode ser vazio");
            }
            if (!razaoSocialTrim.matches(".*[a-zA-ZÀ-ÿ].*")) {
                throw new RegraNegocioException("Nome deve conter letras válidas");
            }

            String nomeFantasiaTrim = usuarioPessoaJuridica.getRazaoSocial().trim();
            if (nomeFantasiaTrim.isBlank()) {
                throw new RegraNegocioException("Nome não pode ser vazio");
            }
            if (!nomeFantasiaTrim.matches(".*[a-zA-ZÀ-ÿ].*")) {
                throw new RegraNegocioException("Nome deve conter letras válidas");
            }

            if (usuarioPessoaJuridica.getId() == null && repository.existsByRazaoSocialIgnoreCase(usuarioPessoaJuridica.getRazaoSocial())) {
                throw new RegraNegocioException("Razão social já cadastrada");
            }

            if (usuarioPessoaJuridica.getId() == null && repository.existsByCnpj(usuarioPessoaJuridica.getCnpj())) {
                throw new RegraNegocioException("CNPJ já cadastrado");
            }

            if (usuarioPessoaJuridica.getId() == null &&
                    repository.existsByInscricaoEstadualMunicipal(
                            usuarioPessoaJuridica.getInscricaoEstadualMunicipal())) {
                throw new RegraNegocioException("Inscrição já cadastrada");
            }
        } else {
            return;
        }
    }
}
