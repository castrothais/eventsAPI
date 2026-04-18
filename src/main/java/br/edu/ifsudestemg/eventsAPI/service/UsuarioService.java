package br.edu.ifsudestemg.eventsAPI.service;

import br.edu.ifsudestemg.eventsAPI.api.exeption.RegraNegocioException;
import br.edu.ifsudestemg.eventsAPI.model.entity.Usuario;
import br.edu.ifsudestemg.eventsAPI.model.repository.UsuarioRepository;
import br.edu.ifsudestemg.eventsAPI.service.validator.UsuarioValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UsuarioService{
    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private List<UsuarioValidator> validators;

    public UsuarioService(UsuarioRepository repository, List<UsuarioValidator> validators) {
        this.repository = repository;
        this.validators = validators;
    }

    public List<Usuario> getUsuarios() {
        return repository.findAll();
    }

    public Optional<Usuario> getUsuariosById(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public Usuario salvar(Usuario usuario) {
        validar(usuario);
        return repository.save(usuario);
    }

    @Transactional
    public void excluir(Usuario usuario) {
        Objects.requireNonNull(usuario.getId());
        repository.delete(usuario);
    }

    public void validar(Usuario usuario) {
        for (var validator : validators) {
            validator.validar(usuario);
        }

        validarSenhaForte(usuario.getSenha());

        if (usuario.getId() == null && repository.existsByEmailIgnoreCase(usuario.getEmail())) {
            throw new RegraNegocioException("Email já cadastrado");
        }
    }

    public void validarSenhaForte(String senha){
        if (senha == null || senha.length() < 6) {
            throw new RegraNegocioException("Senha deve ter no mínimo 6 caracteres");
        }
        boolean numero = false;
        boolean letraMaiuscula = false;
        boolean letraMinuscula = false;
        boolean simbolo = false;
        for (char c : senha.toCharArray()) {
            if(Character.isDigit(c)){
                numero = true;
            } else if (Character.isUpperCase(c)) {
                letraMaiuscula = true;
            } else if (Character.isLowerCase(c)) {
                letraMinuscula = true;
            } else {
                simbolo = true;
            }
        }
        if (!numero || !letraMaiuscula || !letraMinuscula || !simbolo ) {
            throw new RegraNegocioException("Senha deve conter número, maiúscula, minúscula e símbolo");
        }
    }
}