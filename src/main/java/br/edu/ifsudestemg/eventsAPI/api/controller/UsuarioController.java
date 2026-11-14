package br.edu.ifsudestemg.eventsAPI.api.controller;

import br.edu.ifsudestemg.eventsAPI.api.dto.CredenciaisDTO;
import br.edu.ifsudestemg.eventsAPI.api.dto.TokenDTO;
import br.edu.ifsudestemg.eventsAPI.api.exeption.SenhaInvalidaException;
import br.edu.ifsudestemg.eventsAPI.security.JwtService;
import br.edu.ifsudestemg.eventsAPI.api.dto.UsuarioDTO;
import br.edu.ifsudestemg.eventsAPI.api.exeption.RegraNegocioException;
import br.edu.ifsudestemg.eventsAPI.model.entity.Perfil;
import br.edu.ifsudestemg.eventsAPI.model.entity.Usuario;
import br.edu.ifsudestemg.eventsAPI.model.entity.UsuarioPessoaFisica;
import br.edu.ifsudestemg.eventsAPI.model.entity.UsuarioPessoaJuridica;
import br.edu.ifsudestemg.eventsAPI.service.UsuarioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/usuario")
@RequiredArgsConstructor
@Tag(name = "Usuário", description = "API de Usuário")
public class UsuarioController {

    private final UsuarioService service;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/me")
    @Operation(summary = "Obter dados do usuário autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<UsuarioDTO> getUsuarioAutenticado(
            Authentication authentication) {

        try {
            Usuario usuario = service.buscarPorEmail(authentication.getName());

            return ResponseEntity.ok(UsuarioDTO.create(usuario));

        } catch (UsernameNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }



    @GetMapping()
    @Operation(summary = "Obter usuários")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuários encontrados"),
            @ApiResponse(responseCode = "404", description = "Nenhum usuário encontrado")
    })
    public ResponseEntity get() {
        List<Usuario> usuarios = service.getUsuarios();
        return ResponseEntity.ok(usuarios.stream().map(UsuarioDTO::create).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter informações de um usuário")
    public ResponseEntity<?> get(
            @PathVariable("id") @Parameter(description = "Id do usuário") Long id,
            Authentication authentication) {

        Optional<Usuario> usuario = service.getUsuariosById(id);

        if (usuario.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        boolean administrador = authentication.getAuthorities()
                .stream()
                .anyMatch(autoridade ->
                        autoridade.getAuthority().equals("ROLE_ADMINISTRADOR"));

        boolean proprioUsuario = usuario.get().getEmail()
                .equalsIgnoreCase(authentication.getName());

        if (!administrador && !proprioUsuario) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(UsuarioDTO.create(usuario.get()));
    }

    @PostMapping()
    @Operation(summary = "Salvar usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário salvo com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou regra de negócio violada para usuário")
    })
    public ResponseEntity post(@Valid @RequestBody UsuarioDTO dto) {
        try {
            if (dto.getSenha() == null || dto.getSenha().isBlank()) {
                return ResponseEntity.badRequest().body("Senha inválida");
            }

            service.validarSenhaForte(dto.getSenha());

            Usuario usuario = converter(dto);

            usuario.setPerfis(List.of(Perfil.PARTICIPANTE));

            usuario.setSenha(passwordEncoder.encode(dto.getSenha()));

            usuario = service.salvar(usuario);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(UsuarioDTO.create(usuario));

        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    @Operation(summary = "Atualizar dados de usuário")
    public ResponseEntity<?> atualizar(
            @PathVariable("id") Long id,
            @Valid @RequestBody UsuarioDTO dto,
            Authentication authentication) {

        Optional<Usuario> usuarioExistenteOpt = service.getUsuariosById(id);

        if (usuarioExistenteOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Usuario usuarioExistente = usuarioExistenteOpt.get();

        boolean administrador = isAdministrador(authentication);
        boolean proprioUsuario = isProprioUsuario(usuarioExistente, authentication);

        if (!administrador && !proprioUsuario) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            Usuario usuario = converter(dto);

            usuario.setPerfis(usuarioExistente.getPerfis());

            if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
                service.validarSenhaForte(dto.getSenha());
                usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
            } else {
                usuario.setSenha(usuarioExistente.getSenha());
            }

            usuario.setId(id);
            usuario = service.salvar(usuario);

            return ResponseEntity.ok(UsuarioDTO.create(usuario));

        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("{id}")
    @Operation(summary = "Excluir usuário")
    public ResponseEntity<?> excluir(
            @PathVariable("id") Long id,
            Authentication authentication) {

        Optional<Usuario> usuario = service.getUsuariosById(id);

        if (usuario.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        boolean administrador = isAdministrador(authentication);
        boolean proprioUsuario = isProprioUsuario(usuario.get(), authentication);

        if (!administrador && !proprioUsuario) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            service.excluir(usuario.get());
            return ResponseEntity.noContent().build();

        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public Usuario converter(UsuarioDTO dto) {
        ModelMapper modelMapper = new ModelMapper();

        if (dto.getTipoUsuario() == null) {
            throw new RegraNegocioException("Tipo de usuário é obrigatório");
        }

        return switch (dto.getTipoUsuario()) {
            case PF -> modelMapper.map(dto, UsuarioPessoaFisica.class);
            case PJ -> modelMapper.map(dto, UsuarioPessoaJuridica.class);
        };
    }

    @PostMapping("/auth")
    @Operation(summary = "Autenticar usuário (Sign In)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Autenticação realizada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    })
    public ResponseEntity<?> autenticar(@RequestBody CredenciaisDTO credenciaisDTO){
        try {
            Usuario usuario = service.buscarPorEmail(credenciaisDTO.getEmail());

            UserDetails usuarioAutenticado = service.autenticar(credenciaisDTO.getSenha(), credenciaisDTO.getEmail());
            String token = jwtService.gerarToken(usuario);

            return ResponseEntity.ok(new TokenDTO(usuario.getEmail(), token));

        } catch (UsernameNotFoundException | SenhaInvalidaException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email ou senha inválidos");
        }
    }

    private boolean isAdministrador(Authentication authentication) {
        return authentication.getAuthorities()
                .stream()
                .anyMatch(autoridade ->
                        autoridade.getAuthority().equals("ROLE_ADMINISTRADOR"));
    }

    private boolean isProprioUsuario(
            Usuario usuario,
            Authentication authentication) {

        return usuario.getEmail().equalsIgnoreCase(authentication.getName());
    }
}
