package br.edu.ifsudestemg.eventsAPI.service.validator;

import br.edu.ifsudestemg.eventsAPI.model.entity.Usuario;
import br.edu.ifsudestemg.eventsAPI.model.entity.UsuarioPessoaFisica;
import org.springframework.stereotype.Component;

@Component
public class UsuarioPessoaFisicaValidator implements UsuarioValidator{
    @Override
    public void validar(Usuario usuario) {

        if (usuario instanceof UsuarioPessoaFisica usuarioPessoaFisica) {
        }
    }
}
