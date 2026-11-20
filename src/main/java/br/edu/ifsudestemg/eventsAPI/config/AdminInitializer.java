
package br.edu.ifsudestemg.eventsAPI.config;

import br.edu.ifsudestemg.eventsAPI.model.entity.Endereco;
import br.edu.ifsudestemg.eventsAPI.model.entity.Perfil;
import br.edu.ifsudestemg.eventsAPI.model.entity.UsuarioPessoaFisica;
import br.edu.ifsudestemg.eventsAPI.model.repository.UsuarioRepository;
import br.edu.ifsudestemg.eventsAPI.service.UsuarioService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class AdminInitializer {

    // ${app.admin.###}  é para indicar que as informações serão lidas do application.properties.
    @Value("${app.admin.nome}")
    private String nome;

    @Value("${app.admin.cpf}")
    private String cpf;

    @Value("${app.admin.data}")
    private String data;

    @Value("${app.admin.genero}")
    private String genero;

    @Value("${app.admin.celular}")
    private String celular;

    @Value("${app.admin.email}")
    private String email;

    @Value("${app.admin.senha}")
    private String senha;

    @Value("${app.admin.endereco.cep}")
    private String cep;

    @Value("${app.admin.endereco.estado}")
    private String estado;

    @Value("${app.admin.endereco.cidade}")
    private String cidade;

    @Value("${app.admin.endereco.bairro}")
    private String bairro;

    @Value("${app.admin.endereco.logradouro}")
    private String logradouro;

    @Value("${app.admin.endereco.numero}")
    private String numero;

    @Value("${app.admin.endereco.complemento:}")
    private String complemento;

    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;

    public AdminInitializer(
            UsuarioRepository usuarioRepository,
            UsuarioService usuarioService,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public ApplicationRunner inicializarAdministrador() {
        return args -> {
            boolean administradorExiste =
                    usuarioRepository.existsByPerfil(Perfil.ADMINISTRADOR);

            if (administradorExiste) {
                System.out.println("Administrador inicial já cadastrado.");
            } else {
                criarAdministrador();
            }
        };
    }

    private void criarAdministrador() {

        Endereco endereco = new Endereco(
                cep,
                estado,
                cidade,
                bairro,
                logradouro,
                numero,
                complemento
        );

        UsuarioPessoaFisica administrador = new UsuarioPessoaFisica();

        administrador.setNome(nome);
        administrador.setCpf(cpf);
        administrador.setData(LocalDate.parse(data));
        administrador.setGenero(genero);

        administrador.setCelular(celular);
        administrador.setEmail(email);
        administrador.setEndereco(endereco);

        administrador.setPerfis(List.of(Perfil.ADMINISTRADOR));

        usuarioService.validarSenhaForte(senha);
        administrador.setSenha(passwordEncoder.encode(senha));

        usuarioService.salvar(administrador);

        System.out.println("Administrador inicial cadastrado com sucesso!");
    }
}
