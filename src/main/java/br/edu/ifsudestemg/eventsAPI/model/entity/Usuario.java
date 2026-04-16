package br.edu.ifsudestemg.eventsAPI.model.entity;

import jakarta.persistence.*;
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
    private String senha;
    private String celular;
    private String email;


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