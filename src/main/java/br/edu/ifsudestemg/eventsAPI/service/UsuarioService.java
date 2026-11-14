package br.edu.ifsudestemg.eventsAPI.service;

import br.edu.ifsudestemg.eventsAPI.api.exeption.RegraNegocioException;
import br.edu.ifsudestemg.eventsAPI.api.exeption.SenhaInvalidaException;
import br.edu.ifsudestemg.eventsAPI.model.entity.Usuario;
import br.edu.ifsudestemg.eventsAPI.model.entity.Perfil;
import br.edu.ifsudestemg.eventsAPI.model.repository.UsuarioRepository;
import br.edu.ifsudestemg.eventsAPI.service.validator.UsuarioValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UsuarioService implements UserDetailsService{
    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private List<UsuarioValidator> validators;

    @Autowired
    private CarteiraService carteiraService;

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

        boolean novoUsuario = usuario.getId() == null;

        usuario = repository.save(usuario);

        if (novoUsuario) {
            carteiraService.obterOuCriarCarteira(usuario);
        }

        return usuario;
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

    public UserDetails autenticar(String senha, String email){
        //UserDetails user = loadUserByUsername(usuario.getEmail());
        //boolean senhasBatem = encoder.matches(usuario.getSenha(), user.getPassword());
        UserDetails user = loadUserByUsername(email);
        boolean senhasBatem = encoder.matches(senha, user.getPassword());

        if (senhasBatem){
            return user;
        }
        throw new SenhaInvalidaException();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        String[] roles = usuario.getPerfis()
                .stream()
                .map(Perfil::name)
                .toArray(String[]::new);

        return User
                .builder()
                .username(usuario.getEmail())
                .password(usuario.getSenha())
                .roles(roles)
                .build();
    }

    public Usuario buscarPorEmail(String email){
        return repository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("Usuário não encontrado"));
    }
}
