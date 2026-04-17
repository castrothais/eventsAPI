package br.edu.ifsudestemg.eventsAPI.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)

public abstract class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Senha é obrigatório")
    @Size(min = 6, max = 100, message = "A senha deve ter entre 6 e 100 caracteres")
    private String senha;

    @NotBlank(message = "Telefone é obrigatório")
    @Pattern(regexp = "^\\(\\d{2}\\) 9\\d{4}-\\d{4}$", message = "Formato: (99) 99999-9999")
    private String celular;

    @NotBlank(message = "Email é obrigatório")
    @Email
    private String email;

    @NotNull(message = "O evento deve estar associada a um endereço")
    @Embedded
    private Endereco endereco;

    @ElementCollection(targetClass = Perfil.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "usuario_perfil",
            joinColumns = @JoinColumn(name = "usuario_id")
    )
    @Column(name = "perfil")
    private List<Perfil> perfis;

    public abstract String getNomeUsuario();

    //public abstract String getDocumentoUsuario();
}