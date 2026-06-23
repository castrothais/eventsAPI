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
import org.springframework.web.bind.annotation.*;

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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity get( @PathVariable("id") @Parameter(description = "Id do usuário") Long id) {
        Optional<Usuario> usuario = service.getUsuariosById(id);
        if (!usuario.isPresent()) {
            return new ResponseEntity("Usuario não encontrado", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(usuario.map(UsuarioDTO::create));
    }

    @PostMapping()
    @Operation(summary = "Salvar usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário salvo com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou regra de negócio violada para usuário")
    })
    public ResponseEntity post(@Valid @RequestBody UsuarioDTO dto) {
        try {

            Usuario usuario = converter(dto);

            usuario = service.salvar(usuario);

            return new ResponseEntity(usuario, HttpStatus.CREATED);

        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    @Operation(summary = "Atualizar dados de usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Falha ao atualizar usuário")
    })
    public ResponseEntity atualizar( @PathVariable("id") @Parameter(description = "Id do usuário") Long id, @Valid @RequestBody UsuarioDTO dto ) {

        if (!service.getUsuariosById(id).isPresent()) {
            return new ResponseEntity("Usuario não encontrado", HttpStatus.NOT_FOUND);
        }

        try {

            Usuario usuario = converter(dto);

            usuario.setId(id);

            service.salvar(usuario);

            return ResponseEntity.ok(usuario);

        } catch (RegraNegocioException e) {

            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("{id}")
    @Operation(summary = "Excluir usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuário excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Falha ao excluir usuário")
    })
    public ResponseEntity excluir(@PathVariable("id") @Parameter(description = "Id do usuário") Long id) {
        Optional<Usuario> usuario = service.getUsuariosById(id);
        if (!usuario.isPresent()) {
            return new ResponseEntity("Usuario não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            service.excluir(usuario.get());
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public Usuario converter(UsuarioDTO dto) {
        ModelMapper modelMapper = new ModelMapper();

        if (dto.getTipoUsuario() == null) {
            throw new RegraNegocioException("Tipo de usuário é obrigatório");
        }

        Usuario usuario = switch (dto.getTipoUsuario()) {

            case PF -> modelMapper.map(dto, UsuarioPessoaFisica.class);

            case PJ -> modelMapper.map(dto, UsuarioPessoaJuridica.class);
        };

        if (dto.getPerfis() != null && !dto.getPerfis().isEmpty()) {

            usuario.setPerfis(
                    dto.getPerfis()
                            .stream()
                            .map(Perfil::valueOf)
                            .collect(java.util.stream.Collectors.toList())
            );

        } else {
            usuario.setPerfis(List.of(Perfil.PARTICIPANTE));
        }

        return usuario;

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

            UserDetails usuarioAutenticado = service.autenticar(usuario);
            String token = jwtService.gerarToken(usuario);

            return ResponseEntity.ok(new TokenDTO(usuario.getEmail(), token));

        } catch (UsernameNotFoundException | SenhaInvalidaException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email ou senha inválidos");
        }
    }
}
