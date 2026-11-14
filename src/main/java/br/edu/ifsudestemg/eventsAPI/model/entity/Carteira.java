
package br.edu.ifsudestemg.eventsAPI.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Carteira {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @DecimalMin("0.0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal saldo = BigDecimal.ZERO;

    @NotNull
    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;
}
